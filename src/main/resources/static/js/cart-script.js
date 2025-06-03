// Cart Page JavaScript
document.addEventListener("DOMContentLoaded", function () {
  initializeCartPage();
});

// Global variables
let cartData = {
  items: [],
  subtotal: 0,
  discount: 0,
  tax: 0,
  shippingFee: 30000,
  total: 0,
};

function initializeCartPage() {
  loadCartItems();
  loadRecommendedProducts();
  initializeFormValidation();
  calculateTotals();
}

// Load Cart Items
function loadCartItems() {
  showLoading(true);
  fetch("/cart/api/items", {
    headers: {
      "X-Requested-With": "XMLHttpRequest",
    },
  })
    .then((response) => response.json())
    .then((data) => {
      showLoading(false);
      if (data.success) {
        cartData.items = data.items;
        renderCartItems(data.items);
        updateCartCount(data.items.length);
        calculateTotals();
      } else {
        showEmptyCart();
      }
    })
    .catch((error) => {
      showLoading(false);
      console.error("Error loading cart:", error);
      showEmptyCart();
    });
}

function renderCartItems(items) {
  const cartItemsContainer = document.getElementById("cartItems");
  const emptyCart = document.getElementById("emptyCart");

  if (!items || items.length === 0) {
    cartItemsContainer.style.display = "none";
    emptyCart.style.display = "block";
    return;
  }

  cartItemsContainer.style.display = "block";
  emptyCart.style.display = "none";

  const itemsHTML = items
    .map(
      (item) => `
      <div class="cart-item" data-item-id="${item.id}">
          <div class="item-image">
              <img src="${item.imageUrl}" alt="${item.title}" 
                   onerror="this.src='/images/placeholder.png'">
          </div>
          <div class="item-details">
              <h3 class="item-title">${item.title}</h3>
              <p class="item-author">Tác giả: ${item.author}</p>
              <div class="item-price">
                  <span class="current-price">${formatCurrency(item.price)}</span>
                  ${
                    item.originalPrice
                      ? `<span class="original-price">${formatCurrency(item.originalPrice)}</span>`
                      : ""
                  }
              </div>
          </div>
          <div class="item-quantity">
              <div class="quantity-controls">
                  <button class="qty-btn" onclick="updateQuantity(${item.id}, -1)">
                      <i class="ri-subtract-line"></i>
                  </button>
                  <input type="number" class="qty-input" value="${item.quantity}" min="1" max="10" 
                         onchange="updateQuantity(${item.id}, this.value, true)">
                  <button class="qty-btn" onclick="updateQuantity(${item.id}, 1)">
                      <i class="ri-add-line"></i>
                  </button>
              </div>
          </div>
          <div class="item-total">
              <span class="total-price">${formatCurrency(item.price * item.quantity)}</span>
          </div>
          <div class="item-actions">
              <button class="action-btn remove-btn" onclick="removeItem(${item.id})" 
                      title="Xóa khỏi giỏ">
                  <i class="ri-delete-bin-line"></i>
              </button>
          </div>
      </div>
    `
    )
    .join("");

  cartItemsContainer.innerHTML = itemsHTML;
}

function updateQuantity(itemId, change, isAbsolute = false) {
  showLoading(true);

  const quantity = isAbsolute ? parseInt(change) : change;
  fetch("/cart/api/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Requested-With": "XMLHttpRequest",
    },
    body: JSON.stringify({
      bookId: itemId,
      quantity: quantity,
    }),
  })
    .then((response) => response.json())
    .then((data) => {
      showLoading(false);
      if (data.success) {
        loadCartItems(); // Reload cart to reflect changes
        showToast("Đã cập nhật số lượng", "success");
      } else {
        showToast(data.message || "Có lỗi xảy ra", "error");
      }
    })
    .catch((error) => {
      showLoading(false);
      console.error("Error:", error);
      showToast("Có lỗi xảy ra khi cập nhật", "error");
    });
}

function removeItem(itemId) {
  showModal("Xác nhận xóa", "Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?", () => {
    showLoading(true);
    fetch(`/cart/api/remove/${itemId}`, {
      method: "DELETE",
      headers: {
        "X-Requested-With": "XMLHttpRequest",
      },
    })
      .then((response) => response.json())
      .then((data) => {
        showLoading(false);
        if (data.success) {
          loadCartItems();
          showToast("Đã xóa sản phẩm khỏi giỏ hàng", "success");
        } else {
          showToast(data.message || "Có lỗi xảy ra", "error");
        }
      })
      .catch((error) => {
        showLoading(false);
        console.error("Error:", error);
        showToast("Có lỗi xảy ra", "error");
      });
  });
}

function clearCart() {
  if (!cartData.items.length) {
    showToast("Giỏ hàng đã trống", "warning");
    return;
  }

  showModal("Xác nhận xóa", "Bạn có chắc chắn muốn xóa tất cả sản phẩm trong giỏ hàng?", () => {
    showLoading(true);
    fetch("/cart/api/clear", {
      method: "POST",
      headers: {
        "X-Requested-With": "XMLHttpRequest",
      },
    })
      .then((response) => response.json())
      .then((data) => {
        showLoading(false);
        if (data.success) {
          showEmptyCart();
          showToast("Đã xóa tất cả sản phẩm", "success");
        } else {
          showToast(data.message || "Có lỗi xảy ra", "error");
        }
      })
      .catch((error) => {
        showLoading(false);
        console.error("Error:", error);
        showToast("Có lỗi xảy ra", "error");
      });
  });
}

function showEmptyCart() {
  document.getElementById("cartItems").style.display = "none";
  document.getElementById("emptyCart").style.display = "block";
  cartData.items = [];
  calculateTotals();
}

function calculateTotals() {
  const subtotal = cartData.items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  const discount = calculateDiscount(subtotal);
  const tax = Math.round((subtotal - discount) * 0.1);
  const total = subtotal - discount + tax + cartData.shippingFee;

  document.getElementById("itemCount").textContent = cartData.items.length;
  document.getElementById("subtotal").textContent = formatCurrency(subtotal);
  document.querySelector(".discount-amount").textContent = `-${formatCurrency(discount)}`;
  document.getElementById("shippingFee").textContent = formatCurrency(cartData.shippingFee);
  document.getElementById("tax").textContent = formatCurrency(tax);
  document.getElementById("totalAmount").textContent = formatCurrency(total);

  cartData = { ...cartData, subtotal, discount, tax, total };
}

function calculateDiscount(subtotal) {
  if (subtotal >= 1000000) return Math.round(subtotal * 0.1);
  if (subtotal >= 500000) return Math.round(subtotal * 0.05);
  return 0;
}

function proceedToCheckout() {
  if (cartData.items.length === 0) {
    showToast("Giỏ hàng của bạn đang trống", "warning");
    return;
  }

  const orderSummary = document.querySelector(".order-summary");
  const shippingSection = document.getElementById("shippingSection");

  if (!orderSummary || !shippingSection) {
    console.error("Required elements not found");
    showToast("Có lỗi xảy ra", "error");
    return;
  }

  orderSummary.classList.add("hidden");
  shippingSection.classList.add("active");
}

function backToSummary() {
  const orderSummary = document.querySelector(".order-summary");
  const shippingSection = document.getElementById("shippingSection");

  orderSummary.classList.remove("hidden");
  shippingSection.classList.remove("active");
}

function confirmOrder(event) {
  event.preventDefault();

  const form = document.getElementById("shippingForm");
  if (!form.checkValidity()) {
    form.reportValidity();
    return;
  }

  const formData = new FormData(form);
  const orderData = {
    items: cartData.items,
    shipping: {
      fullName: formData.get("fullName"),
      phone: formData.get("phone"),
      email: formData.get("email"),
      address: formData.get("address"),
      province: formData.get("province"),
      district: formData.get("district"),
      method: formData.get("shippingMethod"),
      note: formData.get("note"),
    },
    payment: document.querySelector('input[name="paymentMethod"]:checked')?.value,
    totals: {
      subtotal: cartData.subtotal,
      discount: cartData.discount,
      tax: cartData.tax,
      shipping: cartData.shippingFee,
      total: cartData.total,
    },
  };

  if (!orderData.payment) {
    showToast("Vui lòng chọn phương thức thanh toán", "warning");
    return;
  }

  showModal(
    "Xác nhận đặt hàng",
    `Tổng tiền: ${formatCurrency(cartData.total)}<br>
     Phương thức thanh toán: ${orderData.payment}<br><br>
     Bạn có chắc chắn muốn đặt hàng?`,
    () => submitOrder(orderData)
  );
}

async function submitOrder(orderData) {
  try {
    showLoading(true);
    const response = await fetch("/cart/api/checkout", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Requested-With": "XMLHttpRequest",
      },
      body: JSON.stringify(orderData),
    });

    const result = await response.json();
    if (result.success) {
      showToast("Đặt hàng thành công!", "success");
      setTimeout(() => {
        window.location.href = `/orders/${result.orderId}`;
      }, 1500);
    } else {
      showToast(result.message || "Có lỗi xảy ra khi đặt hàng", "error");
    }
  } catch (error) {
    console.error("Error:", error);
    showToast("Có lỗi xảy ra khi đặt hàng", "error");
  } finally {
    showLoading(false);
  }
}

function formatCurrency(amount) {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
}

function showToast(message, type = "success") {
  const toast = document.createElement("div");
  toast.className = `toast toast-${type}`;
  toast.textContent = message;

  const container = document.getElementById("toastContainer");
  container.appendChild(toast);

  setTimeout(() => {
    toast.classList.add("show");
    setTimeout(() => {
      toast.classList.remove("show");
      setTimeout(() => toast.remove(), 300);
    }, 3000);
  }, 100);
}

function showModal(title, message, onConfirm) {
  const modal = new bootstrap.Modal(document.getElementById("confirmModal"));
  document.getElementById("modalTitle").textContent = title;
  document.getElementById("modalMessage").innerHTML = message;

  const confirmBtn = document.getElementById("modalConfirm");
  confirmBtn.onclick = () => {
    modal.hide();
    onConfirm();
  };

  modal.show();
}

function showLoading(show) {
  const overlay = document.getElementById("loadingOverlay");
  overlay.style.display = show ? "flex" : "none";
}
