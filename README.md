# SplitEase ⚡ Shared Expense Tracker

[![Java 17](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot 3.2.5](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](http://localhost:8080/api/swagger-ui.html)

A premium RESTful API and interactive dashboard for tracking shared expenses among groups of friends, roommates, or travel companions. SplitEase automatically calculates net balances and computes the **optimal, minimized set of transactions** required to settle all debts.

---

## 🌟 Core Features

- 👥 **Group Management**: Seamlessly create expense groups (e.g., "Goa Trip 2024", "Room rent").
- 👤 **Soft-Delete for Members**: Members can be deactivated. They are excluded from future calculations while preserving all past historical transaction and balance integrity.
- ⚖️ **Flexible Split Methods**:
  - **EQUAL**: Divides the expense evenly among all active members, with precision remainder rounding (e.g. splitting ₹100 among 3 people distributes ₹33.33 to two members and ₹33.34 to the first member to avoid penny loss).
  - **CUSTOM**: Allocate precise manual amounts for each member, validated to match the total exactly.
- 💸 **Debt Minimization Algorithm**: Reduces the total number of bank transfers or payments using an optimized Greedy Priority Queue approach.
- 📜 **Interactive Dashboard**: A glassmorphic web dashboard integrated directly at the `/api/` endpoint, displaying real-time metrics, member statuses, interactive modals, and the expense ledger.

---

## 🛠️ Tech Stack

- **Backend Framework**: Spring Boot 3 (Java 17)
- **Database**: PostgreSQL (relational mappings, aggregate SQL queries)
- **ORM & Data Layer**: Spring Data JPA / Hibernate
- **Interactive UI**: Semantic HTML5, CSS3 Custom Properties (Vanilla CSS with Glassmorphism Theme), Vanilla JS
- **API Documentation**: Springdoc OpenAPI / Swagger UI

---

## 🧮 Debt Minimization Algorithm

When multiple expenses are logged, a complex web of debts is created. SplitEase uses a **Greedy Priority Queue** algorithm to minimize bank transfers.

### Algorithm Flow
1. Compute `netBalance = totalPaid - totalOwed` for each group member.
2. Separate members into two groups:
   - **Creditors**: `netBalance > 0` (sorted highest to lowest).
   - **Debtors**: `netBalance < 0` (sorted highest absolute debt to lowest).
3. Pop the top creditor `C` and top debtor `D`.
4. Settle the amount: `settled = min(C.balance, |D.balance|)`.
5. Log the transaction: **`D` pays `C` → `settled`**.
6. Update their balances. Re-enqueue whoever still has a remaining balance.
7. Repeat until all balances are settled to zero.

### Example Walkthrough
| Member | Paid | Owes | Net Balance | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Rahul** | ₹900 | ₹600 | **+₹300** | Gets back ₹300 |
| **Priya** | ₹600 | ₹600 | **₹0** | Fully Settled |
| **Amit** | ₹300 | ₹600 | **-₹300** | Owes ₹300 |

* **Optimal Transfer**: **Amit pays Rahul ₹300** (Only **1 transaction** required instead of 2!).

---

## 🚀 Setup & Execution Guide

### 1. Prerequisites
- **Java JDK 17** or higher
- **PostgreSQL** running locally
- **Maven 3.8+** (Optional — Maven Wrapper is bundled!)

### 2. Configure the Database
Create a database named `spliteasedb` in PostgreSQL.

#### Using scripts in the repository:
- **Windows**: Double-click or run `setup_db.bat`
- **Linux/macOS**: Run `chmod +x setup_db.sh && ./setup_db.sh`

#### Manual SQL:
```sql
CREATE DATABASE spliteasedb;
```

### 3. Verify Database Credentials
If your local PostgreSQL credentials differ from `postgres/root`, update `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
```

### 4. Build and Run the Server
Use the pre-packaged Maven Wrapper to compile and run the project without needing Maven pre-installed.

#### Windows (PowerShell / CMD)
```powershell
.\mvnw.cmd spring-boot:run
```

#### Linux / macOS
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

The application starts on **[http://localhost:8080/api/](http://localhost:8080/api/)**.

---

## 📡 API Reference & Endpoints

All endpoints are hosted under the `/api` prefix path.

### Groups
| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/groups` | Create a new expense group |
| `GET` | `/api/groups/{id}` | Get group details (includes all members) |

### Members
| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/groups/{groupId}/members` | Add a new member to the group |
| `DELETE` | `/api/groups/{groupId}/members/{memberId}` | Soft-delete/deactivate a member |

### Expenses
| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/groups/{groupId}/expenses` | Log a new group expense (EQUAL or CUSTOM) |
| `GET` | `/api/groups/{groupId}/expenses` | Retrieve the chronological expense ledger |
| `DELETE` | `/api/groups/{groupId}/expenses/{expenseId}` | Delete an expense and recalculate debts |

### Settlements & Balances
| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/groups/{groupId}/balances` | Get detailed net balances for all members |
| `GET` | `/api/groups/{groupId}/settlements` | Get minimized optimal settlement transactions |

---

## 💻 Interactive Sandbox (cURL commands)

Here are complete API payloads you can try out using curl:

### 1. Create a Group
```bash
curl -X POST http://localhost:8080/api/groups \
  -H "Content-Type: application/json" \
  -d '{"name": "Weekend Getaway", "description": "Manali Trip 2026"}'
```

### 2. Add a Member
```bash
curl -X POST http://localhost:8080/api/groups/1/members \
  -H "Content-Type: application/json" \
  -d '{"name": "Sneha", "email": "sneha@example.com"}'
```

### 3. Log a CUSTOM Split Expense
```bash
curl -X POST http://localhost:8080/api/groups/1/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Cab Rental",
    "amount": 500.00,
    "paidByMemberId": 1,
    "splitType": "CUSTOM",
    "customSplits": [
      {"memberId": 1, "amount": 200.00},
      {"memberId": 2, "amount": 300.00}
    ]
  }'
```

### 4. Fetch Optimal Settlements
```bash
curl http://localhost:8080/api/groups/1/settlements
```

---

## 💡 System Assumptions & Notes
- **Precision Scale**: All money balances are computed with `BigDecimal` maintaining a scale of 2 to avoid precision/rounding loss.
- **Auto-Seeding**: On first startup, a demo group "Goa Trip 2024" with 3 pre-configured members (Rahul, Priya, Amit) and 3 seed expenses is created automatically so you can immediately see the dashboard in action.
