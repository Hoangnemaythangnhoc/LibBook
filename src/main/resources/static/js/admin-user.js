document.addEventListener("DOMContentLoaded", () => {
    loadUsers();

    // Modal handling
    const modal = document.getElementById("staff-modal");
    const openBtn = document.getElementById("create-staff-btn");
    const closeBtn = document.getElementById("close-staff-modal");

    openBtn.addEventListener("click", () => (modal.style.display = "block"));
    closeBtn.addEventListener("click", () => (modal.style.display = "none"));
    window.addEventListener("click", (e) => {
        if (e.target === modal) modal.style.display = "none";
    });

    // Submit create staff
    const form = document.getElementById("staff-form");
    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        data.roleID = parseInt(data.roleID); // roleID must be a number

        try {
            const response = await fetch("/admin/users/create-staff", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });
            if (!response.ok) throw new Error(await response.text());

            alert("Staff account created successfully");
            modal.style.display = "none";
            form.reset();
            loadUsers(); // reload list
        } catch (err) {
            alert("Error: " + err.message);
        }
    });
});

async function loadUsers() {
    const customerTbody = document.getElementById("customer-body");
    const staffTbody = document.getElementById("staff-body");
    customerTbody.innerHTML = "";
    staffTbody.innerHTML = "";

    try {
        const [customers, staff] = await Promise.all([
            fetch("/admin/users/customers").then((res) => res.json()),
            fetch("/admin/users/staff").then((res) => res.json())
        ]);

        customers.forEach((user) => {
            customerTbody.appendChild(createUserRow(user, false));
        });

        staff.forEach((user) => {
            staffTbody.appendChild(createUserRow(user, true));
        });
    } catch (err) {
        console.error("Error loading user list:", err);
    }
}

function createUserRow(user, isStaff) {
    const tr = document.createElement("tr");
    const isActive = user.status === true;

    let rowHtml = `
        <td>#${user.userId}</td>
        <td>${user.userName}</td>
        <td>${user.email}</td>
    `;

    if (isStaff) {
        rowHtml += `<td>${user.roleName}</td>`;
    }

    rowHtml += `
        <td>${formatDate(user.createAt)}</td>
        <td>
            <button class="btn btn-sm btn-primary text-white" onclick="viewProfile(${user.userId})">View Profile</button>
            <button class="btn btn-sm ${isActive ? "btn-danger" : "btn-warning"}" 
                    style="min-width: 70px"
                    onclick="toggleStatus(${user.userId}, ${user.status})">
                ${isActive ? "Ban" : "Unban"}
            </button>
        </td>
    `;

    tr.innerHTML = rowHtml;
    return tr;
}

function formatDate(dateStr) {
    if (!dateStr) return "N/A";
    const d = new Date(dateStr);
    return isNaN(d) ? "N/A" : d.toISOString().split("T")[0];
}

async function toggleStatus(userId, currentStatus) {
    const isActive = currentStatus === true;
    const confirmText = isActive ? "Ban this user?" : "Unban this user?";
    if (!confirm(confirmText)) return;

    const endpoint = `/admin/users/${userId}/${isActive ? "ban" : "unban"}`;

    try {
        const res = await fetch(endpoint, { method: "POST" });
        if (!res.ok) throw new Error(await res.text());
        loadUsers();
    } catch (err) {
        alert("Error: " + err.message);
    }
}

function viewProfile(userId) {
    window.location.href = `/profile?id=${userId}`;
}