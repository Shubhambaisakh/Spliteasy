const BASE_URL = '/api/groups/1'; // Using seeded Goa Trip group ID = 1

// State variables
let activeGroupId = 1;
let members = [];
let expenses = [];
let balances = [];
let settlements = [];

// DOM Elements
document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

// App Initialization
async function initApp() {
    setupEventListeners();
    await syncAllData();
}

// Set up Event Listeners
function setupEventListeners() {
    // Member Modal Controls
    const btnAddMember = document.getElementById('btn-add-member');
    const modalAddMember = document.getElementById('modal-add-member');
    const formAddMember = document.getElementById('form-add-member');
    
    btnAddMember.addEventListener('click', () => showModal('modal-add-member'));
    formAddMember.addEventListener('submit', handleAddMember);

    // Expense Modal Controls
    const btnAddExpense = document.getElementById('btn-add-expense');
    const modalAddExpense = document.getElementById('modal-add-expense');
    const formAddExpense = document.getElementById('form-add-expense');
    
    btnAddExpense.addEventListener('click', () => {
        populatePayerSelector();
        setupCustomSplitsInputs();
        showModal('modal-add-expense');
    });
    formAddExpense.addEventListener('submit', handleAddExpense);

    // Radio button changes for splits
    const radioEqual = document.getElementById('split-type-equal');
    const radioCustom = document.getElementById('split-type-custom');
    const customArea = document.getElementById('custom-splits-area');
    const splitOptionEqual = document.getElementById('split-option-equal');
    const splitOptionCustom = document.getElementById('split-option-custom');

    radioEqual.addEventListener('change', () => {
        customArea.classList.remove('active');
        splitOptionEqual.classList.add('active');
        splitOptionCustom.classList.remove('active');
    });

    radioCustom.addEventListener('change', () => {
        customArea.classList.add('active');
        splitOptionEqual.classList.remove('active');
        splitOptionCustom.classList.add('active');
    });

    // Close Modals
    document.querySelectorAll('.modal-close, .btn-cancel').forEach(btn => {
        btn.addEventListener('click', () => hideAllModals());
    });

    // Backdrops click
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) hideAllModals();
        });
    });
}

// Sync all data from backend
async function syncAllData() {
    try {
        await Promise.all([
            fetchGroupAndMembers(),
            fetchExpenses(),
            fetchBalances(),
            fetchSettlements()
        ]);
        renderAll();
    } catch (err) {
        console.error('Error syncing data from backend:', err);
    }
}

// API Calls
async function fetchGroupAndMembers() {
    const res = await fetch(BASE_URL);
    if (!res.ok) throw new Error('Failed to fetch group details');
    const group = await res.json();
    members = group.members || [];
    
    // Update Group Title and Info Card
    document.getElementById('display-group-name').innerText = group.name;
    document.getElementById('display-group-desc').innerText = group.description;
    
    const dateStr = new Date(group.createdAt).toLocaleDateString('en-IN', {
        day: 'numeric', month: 'short', year: 'numeric'
    });
    document.getElementById('display-group-date').innerText = dateStr;
}

async function fetchExpenses() {
    const res = await fetch(`${BASE_URL}/expenses`);
    if (!res.ok) throw new Error('Failed to fetch expenses');
    expenses = await res.json();
    
    // Calculate total spend
    const total = expenses.reduce((sum, exp) => sum + exp.amount, 0);
    document.getElementById('display-total-spent').innerText = formatCurrency(total);
    document.getElementById('display-expense-count').innerText = expenses.length;
}

async function fetchBalances() {
    const res = await fetch(`${BASE_URL}/balances`);
    if (!res.ok) throw new Error('Failed to fetch balances');
    balances = await res.json();
}

async function fetchSettlements() {
    const res = await fetch(`${BASE_URL}/settlements`);
    if (!res.ok) throw new Error('Failed to fetch settlements');
    settlements = await res.json();
    
    document.getElementById('display-settlement-count').innerText = settlements.length;
}

// Rendering Logic
function renderAll() {
    renderMembersList();
    renderBalancesGrid();
    renderExpensesLedger();
    renderSettlementsList();
}

function renderMembersList() {
    const container = document.getElementById('members-container');
    container.innerHTML = '';

    if (members.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">👤</div>
                <div class="empty-state-text">No members in this group yet.</div>
            </div>
        `;
        return;
    }

    // Filter to active members first, or list all
    members.forEach(member => {
        const item = document.createElement('div');
        item.className = 'member-item';
        
        const initials = member.name ? member.name.charAt(0).toUpperCase() : '?';
        const activeClass = member.isActive ? '' : 'inactive';
        
        item.innerHTML = `
            <div class="member-info">
                <div class="avatar ${activeClass}">${initials}</div>
                <div class="member-details">
                    <span class="member-name">${escapeHtml(member.name)} ${member.isActive ? '' : '(Inactive)'}</span>
                    <span class="member-email">${escapeHtml(member.email)}</span>
                </div>
            </div>
            ${member.isActive ? `
                <div class="member-actions">
                    <button class="btn-icon-only" onclick="deleteMember(${member.id}, '${escapeQuotes(member.name)}')" title="Soft-delete member">
                        ✕
                    </button>
                </div>
            ` : ''}
        `;
        container.appendChild(item);
    });
}

function renderBalancesGrid() {
    const container = document.getElementById('balances-grid');
    container.innerHTML = '';

    if (balances.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">⚖️</div>
                <div class="empty-state-text">No balances to display. Add expenses to calculate balances!</div>
            </div>
        `;
        return;
    }

    balances.forEach(bal => {
        const card = document.createElement('div');
        
        let statusClass = 'settled';
        let statusLabel = 'Settled';
        let valClass = 'neutral';
        let prefix = '';

        if (bal.netBalance > 0.005) {
            statusClass = 'creditor';
            statusLabel = 'Gets back';
            valClass = 'positive';
            prefix = '+';
        } else if (bal.netBalance < -0.005) {
            statusClass = 'debtor';
            statusLabel = 'Owes';
            valClass = 'negative';
        }

        card.className = `balance-card ${statusClass}`;
        
        const initials = bal.memberName ? bal.memberName.charAt(0).toUpperCase() : '?';

        card.innerHTML = `
            <div class="balance-header">
                <div class="avatar">${initials}</div>
                <div class="balance-user-details">
                    <span class="balance-user-name">${escapeHtml(bal.memberName)}</span>
                    <span class="balance-badge ${statusClass}">${statusLabel}</span>
                </div>
            </div>
            <div class="balance-amount-container">
                <span class="balance-label">Net Balance</span>
                <span class="balance-val ${valClass}">${prefix}${formatCurrency(bal.netBalance)}</span>
            </div>
            <div class="balance-breakdown">
                <div>Paid: <strong>${formatCurrency(bal.totalPaid)}</strong></div>
                <div>Owed: <strong>${formatCurrency(bal.totalOwed)}</strong></div>
            </div>
        `;
        container.appendChild(card);
    });
}

function renderExpensesLedger() {
    const container = document.getElementById('expenses-container');
    container.innerHTML = '';

    if (expenses.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">💸</div>
                <div class="empty-state-text">No expenses logged yet. Log your first expense!</div>
            </div>
        `;
        return;
    }

    expenses.forEach(exp => {
        const item = document.createElement('div');
        item.className = 'expense-item';
        
        const dateStr = new Date(exp.createdAt).toLocaleDateString('en-IN', {
            day: 'numeric', month: 'short'
        });

        // Detail tooltip or label summarizing splits
        const splitsCount = exp.splits ? exp.splits.length : 0;
        
        item.innerHTML = `
            <div class="expense-desc-area">
                <div class="expense-icon">💰</div>
                <div class="expense-info-block">
                    <span class="expense-desc">${escapeHtml(exp.description)}</span>
                    <div class="expense-sub">
                        <span>Paid by <strong>${escapeHtml(exp.paidByMemberName)}</strong></span>
                        <span class="bullet"></span>
                        <span>${dateStr}</span>
                        <span class="bullet"></span>
                        <span class="expense-type-badge">${exp.splitType}</span>
                    </div>
                </div>
            </div>
            <div class="expense-amount-area">
                <span class="expense-total">${formatCurrency(exp.amount)}</span>
                <button class="btn-icon-only" onclick="deleteExpense(${exp.id}, '${escapeQuotes(exp.description)}')" title="Delete expense">
                    ✕
                </button>
            </div>
        `;
        container.appendChild(item);
    });
}

function renderSettlementsList() {
    const container = document.getElementById('settlements-container');
    container.innerHTML = '';

    if (settlements.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">🎉</div>
                <div class="empty-state-text" style="max-width: 320px;">Everyone is fully settled! No bank transfers needed.</div>
            </div>
        `;
        return;
    }

    settlements.forEach(settle => {
        const card = document.createElement('div');
        card.className = 'settlement-card';
        card.innerHTML = `
            <div class="settlement-flow">
                <span class="flow-member debtor">${escapeHtml(settle.fromMemberName)}</span>
                <span class="flow-arrow">➔</span>
                <span class="flow-member creditor">${escapeHtml(settle.toMemberName)}</span>
            </div>
            <span class="settlement-amt">${formatCurrency(settle.amount)}</span>
        `;
        container.appendChild(card);
    });
}

// Dropdown Populate in Add Expense
function populatePayerSelector() {
    const selector = document.getElementById('expense-payer');
    selector.innerHTML = '';
    
    const activeMembers = members.filter(m => m.isActive);
    
    if (activeMembers.length === 0) {
        selector.innerHTML = `<option value="">Add active members first</option>`;
        return;
    }
    
    activeMembers.forEach(member => {
        const opt = document.createElement('option');
        opt.value = member.id;
        opt.innerText = member.name;
        selector.appendChild(opt);
    });
}

// Generate rows of inputs for Custom splits
function setupCustomSplitsInputs() {
    const wrapper = document.getElementById('custom-splits-inputs');
    wrapper.innerHTML = '';
    
    const activeMembers = members.filter(m => m.isActive);
    
    activeMembers.forEach(member => {
        const row = document.createElement('div');
        row.className = 'custom-split-row';
        row.innerHTML = `
            <span class="custom-split-name">${escapeHtml(member.name)}</span>
            <input type="number" step="0.01" min="0.01" class="form-input custom-split-input" 
                   data-member-id="${member.id}" placeholder="0.00">
        `;
        wrapper.appendChild(row);
    });
}

// Add Member Submission
async function handleAddMember(e) {
    e.preventDefault();
    const btnSubmit = e.submitter;
    const originalText = btnSubmit.innerHTML;
    
    const nameInput = document.getElementById('member-name');
    const emailInput = document.getElementById('member-email');
    const alert = document.getElementById('alert-add-member');

    alert.classList.remove('active');
    
    // Simple checks
    if (!nameInput.value.trim() || !emailInput.value.trim()) {
        showAlert('alert-add-member', 'Please fill in all fields.');
        return;
    }

    try {
        setBtnLoading(btnSubmit, true);
        const response = await fetch(`${BASE_URL}/members`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name: nameInput.value.trim(),
                email: emailInput.value.trim()
            })
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || 'Failed to add member.');
        }

        // Reset and hide
        nameInput.value = '';
        emailInput.value = '';
        hideAllModals();
        
        // Sync and render
        await syncAllData();
    } catch (err) {
        showAlert('alert-add-member', err.message);
    } finally {
        setBtnLoading(btnSubmit, false, originalText);
    }
}

// Add Expense Submission
async function handleAddExpense(e) {
    e.preventDefault();
    const btnSubmit = e.submitter;
    const originalText = btnSubmit.innerHTML;
    
    const descInput = document.getElementById('expense-desc-input');
    const amountInput = document.getElementById('expense-amount');
    const payerSelect = document.getElementById('expense-payer');
    const isCustom = document.getElementById('split-type-custom').checked;
    const alert = document.getElementById('alert-add-expense');

    alert.classList.remove('active');

    if (!descInput.value.trim() || !amountInput.value || parseFloat(amountInput.value) <= 0) {
        showAlert('alert-add-expense', 'Please enter a valid description and positive amount.');
        return;
    }

    const expenseAmount = parseFloat(parseFloat(amountInput.value).toFixed(2));
    const paidBy = parseInt(payerSelect.value);

    if (!paidBy) {
        showAlert('alert-add-expense', 'Please select a valid member who paid.');
        return;
    }

    const payload = {
        description: descInput.value.trim(),
        amount: expenseAmount,
        paidByMemberId: paidBy,
        splitType: isCustom ? 'CUSTOM' : 'EQUAL',
        customSplits: []
    };

    if (isCustom) {
        let sum = 0;
        const inputs = document.querySelectorAll('.custom-split-input');
        
        for (let input of inputs) {
            const val = parseFloat(input.value);
            const memberId = parseInt(input.dataset.memberId);
            
            if (isNaN(val) || val <= 0) {
                showAlert('alert-add-expense', 'All active members must have a positive custom split amount.');
                return;
            }
            const cleanVal = parseFloat(val.toFixed(2));
            sum += cleanVal;
            payload.customSplits.push({
                memberId: memberId,
                amount: cleanVal
            });
        }
        
        // Validate total sum matches exactly (rounding safe compare)
        if (Math.abs(sum - expenseAmount) > 0.005) {
            showAlert('alert-add-expense', `Custom split amounts must sum exactly to the total expense amount of ${formatCurrency(expenseAmount)}. (Current Sum: ${formatCurrency(sum)})`);
            return;
        }
    }

    try {
        setBtnLoading(btnSubmit, true);
        const response = await fetch(`${BASE_URL}/expenses`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || 'Failed to add expense.');
        }

        // Reset and hide
        descInput.value = '';
        amountInput.value = '';
        document.getElementById('split-type-equal').click(); // trigger reset
        hideAllModals();
        
        await syncAllData();
    } catch (err) {
        showAlert('alert-add-expense', err.message);
    } finally {
        setBtnLoading(btnSubmit, false, originalText);
    }
}

// Soft Delete Member
window.deleteMember = async function(memberId, name) {
    if (!confirm(`Are you sure you want to remove ${name} from future splits? Historical expenses will be preserved.`)) {
        return;
    }
    
    try {
        const response = await fetch(`${BASE_URL}/members/${memberId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) throw new Error('Failed to delete member.');
        await syncAllData();
    } catch (err) {
        alert(err.message);
    }
}

// Delete Expense
window.deleteExpense = async function(expenseId, desc) {
    if (!confirm(`Are you sure you want to delete the expense "${desc}"?`)) {
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/expenses/${expenseId}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Failed to delete expense.');
        await syncAllData();
    } catch (err) {
        alert(err.message);
    }
}

// Helper Functions
function showModal(id) {
    document.getElementById(id).classList.add('active');
}

function hideAllModals() {
    document.querySelectorAll('.modal-overlay').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.alert').forEach(el => el.classList.remove('active'));
}

function showAlert(id, msg) {
    const alert = document.getElementById(id);
    alert.innerText = msg;
    alert.classList.add('active');
}

function setBtnLoading(btn, isLoading, originalText = '') {
    if (isLoading) {
        btn.disabled = true;
        btn.innerHTML = `<span class="loading-indicator"></span>`;
    } else {
        btn.disabled = false;
        btn.innerHTML = originalText;
    }
}

function formatCurrency(val) {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 2
    }).format(val);
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/"/g, '&quot;')
              .replace(/'/g, '&#039;');
}

function escapeQuotes(str) {
    if (!str) return '';
    return str.replace(/'/g, "\\'").replace(/"/g, '\\"');
}
