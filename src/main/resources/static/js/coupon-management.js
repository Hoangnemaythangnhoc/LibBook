let couponModal;
let couponSaveSuccess = false;

document.addEventListener("DOMContentLoaded", function () {
    loadCoupons();

    const modalElement = document.getElementById('couponModal');
    couponModal = new bootstrap.Modal(modalElement);

    modalElement.addEventListener('hidden.bs.modal', function () {
        loadCoupons();

        if (couponSaveSuccess) {
            Swal.fire({
                icon: 'success',
                title: 'Thành công',
                text: 'Lưu coupon thành công!',
                timer: 1500,
                showConfirmButton: false
            });
            couponSaveSuccess = false; // reset flag
        }
    });

    document.getElementById("couponSearchInput").addEventListener("input", function () {
        const searchValue = this.value;
        loadCoupons(searchValue);
    });

    document.getElementById("btnSaveCoupon").addEventListener("click", function () {
        saveCoupon();
    });
});


let currentPage = 0;
let pageSize = 5;

function loadCoupons(search = "", page = 0) {
    currentPage = page;
    const url = `/api/coupon/paged?search=${encodeURIComponent(search)}&page=${page}&size=${pageSize}`;

    fetch(url)
        .then(response => response.json())
        .then(result => {
            renderCouponTable(result.data);
            renderPagination(result.totalPages, result.currentPage);
        })
        .catch(error => {
            console.error("Lỗi khi tải Coupon: ", error);
        });
}


function renderCouponTable(coupons) {
    const tableBody = document.getElementById("couponTableBody");
    tableBody.innerHTML = "";

    coupons.forEach(coupon => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${coupon.couponId}</td>
            <td>${coupon.code}</td>
            <td>${coupon.discountPercent}</td>
            <td>${coupon.quantity}</td>
            <td>${coupon.startDate ? coupon.startDate.replace("T", " ") : ""}</td>
            <td>${coupon.endDate ? coupon.endDate.replace("T", " ") : ""}</td>
            <td>${coupon.isActive ? "Active" : "Inactive"}</td>
            <td>
                <button class="btn btn-sm btn-warning" onclick='showEditCoupon(${JSON.stringify(coupon)})'>Edit</button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

function showEditCoupon(coupon) {
    // Đổ dữ liệu vào form
    document.getElementById("couponId").value = coupon.couponId;
    document.getElementById("code").value = coupon.code;
    document.getElementById("discountPercent").value = coupon.discountPercent;
    document.getElementById("quantity").value = coupon.quantity;
    document.getElementById("startDate").value = coupon.startDate ? coupon.startDate.split("T")[0] : "";
    document.getElementById("endDate").value = coupon.endDate ? coupon.endDate.split("T")[0] : "";
    document.getElementById("isActive").checked = coupon.isActive;

    // Mở modal
    $("#couponModal").modal("show");
}

function openCreateCouponForm() {
    // Clear form để thêm mới
    document.getElementById("couponForm").reset();
    document.getElementById("couponId").value = "";
    $("#couponModal").modal("show");
}

function saveCoupon() {
    const startDateStr = document.getElementById("startDate").value;
    const endDateStr = document.getElementById("endDate").value;

    if (startDateStr && endDateStr) {
        const start = new Date(startDateStr);
        const end = new Date(endDateStr);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // chỉ lấy ngày

        if (start <= today) {
            Swal.fire({
                icon: 'warning',
                title: 'Ngày bắt đầu không hợp lệ',
                text: 'Ngày bắt đầu phải lớn hơn ngày hiện tại.'
            });
            return;
        }

        if (end <= start) {
            Swal.fire({
                icon: 'warning',
                title: 'Ngày kết thúc không hợp lệ',
                text: 'Ngày kết thúc phải lớn hơn ngày bắt đầu.'
            });
            return;
        }
    }

    const couponId = document.getElementById("couponId").value;

    const couponData = {
        code: document.getElementById("code").value,
        discountPercent: parseFloat(document.getElementById("discountPercent").value),
        quantity: parseFloat(document.getElementById("quantity").value),
        startDate: startDateStr ? startDateStr + "T00:00:00" : null,
        endDate: endDateStr ? endDateStr + "T23:59:59" : null,
        isActive: document.getElementById("isActive").checked
    };

    let url = "/api/coupon";
    let method = "POST";

    if (couponId) {
        url = "/api/coupon/" + couponId;
        method = "PUT";
        couponData.couponId = parseInt(couponId);
    }

    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(couponData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Lưu coupon thất bại!");
            }
        })
        .then(() => {
            couponSaveSuccess = true;
            couponModal.hide();
        })
        .catch(error => {
            console.error("Lỗi khi lưu coupon: ", error);
            Swal.fire({
                icon: 'error',
                title: 'Oops!',
                text: 'Có lỗi xảy ra khi lưu.'
            });
        });
}



function renderPagination(totalPages, currentPage) {
    const pagination = document.getElementById("couponPagination");
    pagination.innerHTML = "";

    for (let i = 0; i < totalPages; i++) {
        const li = document.createElement("li");
        li.className = "page-item" + (i === currentPage ? " active" : "");
        li.innerHTML = `<a class="page-link" href="#">${i + 1}</a>`;
        li.addEventListener("click", function (e) {
            e.preventDefault();
            const searchValue = document.getElementById("couponSearchInput").value;
            loadCoupons(searchValue, i);
        });
        pagination.appendChild(li);
    }
}
