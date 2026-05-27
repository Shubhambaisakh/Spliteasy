# SplitEase — Shared Expense Tracker

A REST API for tracking shared expenses among groups of friends on trips or living together.  
Built with **Java 17 + Spring Boot 3 + PostgreSQL**.

---

## How to Run

### 1. Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL running locally

### 2. Create the database
```sql
CREATE DATABASE spliteasedb;
```

### 3. Configure credentials
Edit `src/main/resources/application.properties` if your PostgreSQL username/password differ:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/spliteasedb
spring.datasource.username=postgres
spring.datasource.password=password
```

### 4. Run
```bash
mvn spring-boot:run
```

The app starts on **http://localhost:8080**.  
Swagger UI is available at **http://localhost:8080/swagger-ui.html**.

On first startup, a demo group "Goa Trip 2024" with 3 members and 3 expenses is seeded automatically.

---

## Sample curl Commands

### Groups

**Create a group**
```bash
curl -X POST http://localhost:8080/api/groups \
  -H "Content-Type: application/json" \
  -d '{"name": "Weekend Getaway", "description": "Manali trip"}'
```

**Get a group (with members)**
```bash
curl http://localhost:8080/api/groups/1
```

---

### Members

**Add a member**
```bash
curl -X POST http://localhost:8080/api/groups/1/members \
  -H "Content-Type: application/json" \
  -d '{"name": "Sneha", "email": "sneha@example.com"}'
```

**Remove a member (soft delete)**
```bash
curl -X DELETE http://localhost:8080/api/groups/1/members/2
```

---

### Expenses

**Add an EQUAL split expense**
```bash
curl -X POST http://localhost:8080/api/groups/1/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Hotel",
    "amount": 3000,
    "paidByMemberId": 1,
    "splitType": "EQUAL"
  }'
```

**Add a CUSTOM split expense**
```bash
curl -X POST http://localhost:8080/api/groups/1/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Cab",
    "amount": 500,
    "paidByMemberId": 1,
    "splitType": "CUSTOM",
    "customSplits": [
      {"memberId": 1, "amount": 200},
      {"memberId": 2, "amount": 300}
    ]
  }'
```

**List all expenses in a group**
```bash
curl http://localhost:8080/api/groups/1/expenses
```

**Delete an expense**
```bash
curl -X DELETE http://localhost:8080/api/groups/1/expenses/3
```

---

### Settlements

**Get net balances**
```bash
curl http://localhost:8080/api/groups/1/balances
```

**Get minimized settlement transactions**
```bash
curl http://localhost:8080/api/groups/1/settlements
```

---

## Design Decisions & Assumptions

### Soft Delete for Members
Members are never hard-deleted. Setting `isActive = false` excludes them from future EQUAL splits while preserving all historical expense data. This keeps balance calculations accurate even after someone leaves mid-trip.

### EQUAL Split Rounding
When an amount doesn't divide evenly (e.g., ₹100 among 3 people), each person gets ₹33.33 and the ₹0.01 remainder is assigned to the first member in the list. This ensures the splits always sum exactly to the expense total.

### Balance Calculation
`balance = totalPaid - totalOwed`  
Computed via two aggregate SQL queries per member (sum of expenses paid, sum of splits owed). This is simple and correct; for very large groups a materialized view could be added later.

### Balances Include Inactive Members
An inactive member may still have a non-zero balance from past expenses. They appear in `/balances` and `/settlements` until their debt is settled.

### Custom Split Validation
The API rejects CUSTOM splits where the amounts don't sum exactly to the expense total. This prevents silent data corruption.

### No Authentication
Auth was intentionally skipped. In production you'd add Spring Security with JWT or OAuth2. All endpoints are currently open.

### No Pagination
Expense lists are returned in full (newest first). Pagination would be a straightforward addition with `Pageable` in Spring Data.

### No Currency Support
All amounts are stored as `BigDecimal` with 2 decimal places. Multi-currency support (with exchange rates) was out of scope.

---

## What Was Skipped and Why

| Feature | Reason |
|---|---|
| Authentication / Authorization | Out of scope for a 3-hour exercise; noted as next step |
| Pagination on expense list | Not required; trivial to add with Spring Data `Pageable` |
| Multi-currency | Significant complexity; single-currency assumed |
| Expense editing (PUT) | Not in the spec; delete + re-create is the workaround |
| Email notifications | Infrastructure dependency; out of scope |
| Unit tests beyond context load | Time constraint; service logic is straightforward to unit-test with Mockito |

---

## Debt Minimization Algorithm

### Problem
After N expenses, each member has a net balance. We want the fewest possible bank transfers to settle everyone.

### Algorithm (Greedy / Priority Queue)

1. Compute `netBalance = totalPaid - totalOwed` for each member.
2. Split into two max-heaps:
   - **Creditors** — members with `balance > 0` (others owe them), sorted by largest balance first.
   - **Debtors** — members with `balance < 0` (they owe others), sorted by largest absolute debt first.
3. While both heaps are non-empty:
   - Pop the top creditor `C` and top debtor `D`.
   - `settled = min(C.balance, |D.balance|)`
   - Record transaction: **D pays C `settled`**.
   - Subtract `settled` from both. Re-enqueue whichever side still has a remainder > 0.
4. Return the list of transactions.

### Example (Goa Trip 2024 seed data)

| Member | Paid | Owes | Net |
|--------|------|------|-----|
| Rahul  | ₹900 | ₹600 | **+₹300** |
| Priya  | ₹600 | ₹600 | **₹0** |
| Amit   | ₹300 | ₹600 | **-₹300** |

Creditors: [Rahul +300]  
Debtors:   [Amit  -300]

Iteration 1: settled = min(300, 300) = 300 → **Amit pays Rahul ₹300**  
Both queues empty → done.

**Result: 1 transaction** instead of potentially 2 (Amit→Rahul, Amit→Priya).

### Complexity
- Time: O(N log N) where N = number of members with non-zero balances.
- Space: O(N) for the two heaps.

This greedy approach produces the minimum number of transactions when all balances sum to zero (which they always do in a closed group).
