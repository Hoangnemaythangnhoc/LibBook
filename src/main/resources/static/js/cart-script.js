document.addEventListener("DOMContentLoaded", function () {
  initializeCartPage();
});

// Global variables
let cartData = {
  items: [],
  subtotal: 0,
  discount: 0,
  tax: 0,
  shippingFee: 30000, // Default shipping fee set to standard
  total: 0,
  appliedPromo: null, // Track applied promo code
};



const description = userInSession.phoneNumber.slice(5,10);

const MY_BANK = {
  BANK_ID: "970422", // MB Bank
  ACCOUNT_NO: "7500146341390",
  TEMPLATE: "qr_only",
  ACCOUNT_NAME: "Nguyen Thanh Ha",
};
let lastTransCode = null; // Lưu mã giao dịch thành công
let paymentInterval = null;
let paymentSuccess = false;
let startTime = null;
const MAX_CHECK_DURATION = 5 * 60 * 1000;

function initializeCartPage() {
  loadCartItems();
  calculateTotals();
  initializePaymentMethod();
  initializePromoForm();
  initializeShippingForm();
}

function initializePromoForm() {
  const promoForm = document.getElementById("promoForm");
  if (promoForm) {
    promoForm.addEventListener("submit", (event) => {
      event.preventDefault(); // Prevent form submission
      const promoCode = document.getElementById("promoCode").value.trim();
      if (promoCode) {
        applyPromoCode(promoCode);
      } else {
        showToast("Vui lòng nhập mã giảm giá", "error");
      }
    });
  }
}

function initializeShippingForm() {
  const shippingForm = document.getElementById("shippingForm");
  if (shippingForm) {
    shippingForm.addEventListener("submit", confirmOrder);
  }
}

async function applyPromoCode(promoCode) {
  try {
    // Fetch discount percentage from the API
    const response = await fetch(`/api/coupon/coupon-check-code?code=${encodeURIComponent(promoCode)}`);

    // Check if the response is successful
    if (!response.ok) {
      throw new Error('API request failed');
    }

    const discountPercent = await response.json(); // Expecting an integer (e.g., 20 for 20%)

    // Validate the response
    if (discountPercent > 0) {
      // Apply the promo code
      cartData.appliedPromo = { code: promoCode, discount: discountPercent / 100 }; // Convert to decimal (e.g., 0.2)
      calculateTotals();
      showToast(`Mã ${promoCode} được áp dụng thành công!`, "success");
      document.getElementById("promoCode").value = "";
    } else {
      // Invalid promo code
      showToast("Mã giảm giá không hợp lệ", "error");
      appliedPromos.innerHTML = "";
      cartData.appliedPromo = null;
      calculateTotals();
    }
  } catch (error) {
    // Handle fetch or network errors
    showToast("Đã xảy ra lỗi khi kiểm tra mã giảm giá", "error");
    appliedPromos.innerHTML = "";
    cartData.appliedPromo = null;
    calculateTotals();
    console.error('Error fetching promo code:', error);
  }
}

function initializePaymentMethod() {
  const paymentMethods = document.querySelectorAll('input[name="paymentMethod"]');
  const bankTransferContainer = document.getElementById("bankTransferContainer");

  paymentMethods.forEach((method) => {
    method.addEventListener("change", () => {
      if (method.value === "bank_transfer") {
        bankTransferContainer.style.display = "block";
        bankTransferContainer.classList.add("active");
      } else {
        bankTransferContainer.style.display = "none";
        bankTransferContainer.classList.remove("active");
        stopPaymentCheck();
      }
    });
  });
}

function initializeBankTransfer() {
  if (!userInSession || !userInSession.userId) {
    showToast("Vui lòng đăng nhập để sử dụng thanh toán chuyển khoản", "error");
    document.getElementById("bankTransferContainer").classList.remove("active");
    document.getElementById("bankTransferContainer").style.display = "none";
    document.getElementById("cod").checked = true;
    return;
  }

  const qrCodeUrl =
      "https://img.vietqr.io/image/" +
      MY_BANK.BANK_ID +
      "-" +
      MY_BANK.ACCOUNT_NO +
      "-" +
      MY_BANK.TEMPLATE +
      ".png?" +
      "&amount=" + cartData.total +
      "&addInfo=" +
      userInSession.userId +
      description +
      "&accountName=" +
      MY_BANK.ACCOUNT_NAME;

  document.getElementById("qrCode").src = qrCodeUrl;
  document.getElementById("totalAmountQR").textContent = formatCurrency(cartData.total);
  startPaymentCheck();
}

function copyToClipboard(text) {
  navigator.clipboard.writeText(text).then(() => {
    showToast("Đã sao chép nội dung chuyển khoản", "success");
  }).catch(() => {
    showToast("Không thể sao chép nội dung", "error");
  });
}

function startPaymentCheck() {
  if (paymentSuccess || !userInSession) return;
  startTime = Date.now();
  console.log("⏳ Bắt đầu kiểm tra thanh toán...");
  paymentInterval = setInterval(() => {
    if (Date.now() - startTime > MAX_CHECK_DURATION) {
      stopPaymentCheck();
      showToast("Hết thời gian chờ thanh toán. Vui lòng thử lại.", "error");
      document.getElementById("bankTransferContainer").classList.add("failed");
      document.getElementById("paymentTitle").classList.add("failed");
      document.getElementById("paymentTitle").textContent = "Thanh toán thất bại!";
      return;
    }
    checkPaid();
  }, 5000);
}

function stopPaymentCheck() {
  if (paymentInterval) {
    console.log("⏹️ Dừng kiểm tra thanh toán...");
    clearInterval(paymentInterval);
    paymentInterval = null;
    startTime = null;
  }
}

async function checkPaid() {
  if (paymentSuccess || !userInSession) return;

  const appScriptUrl = "https://script.google.com/macros/s/AKfycbwTfULNVpgezDygzUXfdg-RFYAdbUAddmzlgvIRvdWzzwlvY7IROtuPjyfJLPfDIj6g/exec";
  let retryCount = 0;
  const maxRetries = 3;

  try {
    const response = await fetch(appScriptUrl, { method: "GET" });
    if (!response.ok) throw new Error("Không thể kiểm tra trạng thái thanh toán");

    const data = await response.json();
    const finalRes = data.data;

    for (const bankData of finalRes) {
      const lastPrice = bankData["Giá trị"];
      const lastContent = bankData["Mô tả"];
      const lastTransCodeLocal = bankData["Mã GD"];

      console.log("Dữ liệu từ Google Apps Script:", { lastPrice, lastTransCode: lastTransCodeLocal, lastContent });

      if (String(lastContent).trim().includes(`${userInSession.userId}${description}`)) {
        if (isNaN(Number(lastPrice)) || Number(lastPrice) !== cartData.total) {
          console.warn("Số tiền giao dịch không khớp:", { lastPrice, total: cartData.total });
          continue;
        }

        const paymentData = {
          amount: Number(lastPrice),
          transCode: lastTransCodeLocal,
          userId: Number(userInSession.userId),
        };

        if (isNaN(paymentData.amount) || paymentData.amount <= 0 || !paymentData.transCode) {
          console.error("Dữ liệu thanh toán không hợp lệ:", paymentData);
          showToast("Dữ liệu thanh toán không hợp lệ. Vui lòng thử lại.", "error");
          return;
        }

        console.log("paymentData gửi đi:", paymentData);
        stopPaymentCheck();
        const { ok, data } = await postPayment(paymentData);
        console.log("Phản hồi từ postPayment:", { ok, data });

        if (ok && data.success) {
          paymentSuccess = true;
          lastTransCode = paymentData.transCode;
          const bankTransferContainer = document.getElementById("bankTransferContainer");
          const paymentTitle = document.getElementById("paymentTitle");

          if (bankTransferContainer && paymentTitle) {
            bankTransferContainer.classList.add("success");
            bankTransferContainer.classList.remove("failed");
            paymentTitle.classList.add("success");
            paymentTitle.classList.remove("failed");
            paymentTitle.textContent = "Thanh toán thành công!";
          }
          showToast("Thanh toán thành công! Đang xử lý đơn hàng...", "success");

          const orderData = prepareOrderData();
          if (orderData) {
            await submitOrder(orderData);
          } else {
            showToast("Lỗi: Không thể chuẩn bị dữ liệu đơn hàng", "error");
          }
        } else {
          retryCount++;
          if (retryCount < maxRetries) {
            showToast(`Thanh toán thất bại: ${data.error || "Lỗi không xác định"}. Đang thử lại...`, "warning");
            startPaymentCheck();
          } else {
            const bankTransferContainer = document.getElementById("bankTransferContainer");
            const paymentTitle = document.getElementById("paymentTitle");
            if (bankTransferContainer && paymentTitle) {
              bankTransferContainer.classList.add("failed");
              bankTransferContainer.classList.remove("success");
              paymentTitle.classList.add("failed");
              paymentTitle.classList.remove("success");
              paymentTitle.textContent = "Thanh toán thất bại!";
            }
            showToast(data.error || "Thanh toán thất bại sau nhiều lần thử. Vui lòng thử lại sau.", "error");
          }
        }
        return;
      }
    }
  } catch (err) {
    console.error("Lỗi kiểm tra thanh toán:", err);
    showToast("Không thể xác minh thanh toán. Vui lòng thử lại.", "error");
  } finally {
    showLoading(false);
  }
}

async function postPayment(paymentData) {
  try {
    const response = await fetch("/api/payments", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Requested-With": "XMLHttpRequest",
      },
      body: JSON.stringify(paymentData),
    });
    return { ok: response.ok, data: await response.json() };
  } catch (error) {
    return { ok: false, data: { success: false, error: "Lỗi kết nối mạng" } };
  }
}

async function verifyTransaction() {
  const transactionCode = document.getElementById("transactionCode").value;
  if (!transactionCode) {
    showToast("Vui lòng nhập mã giao dịch", "warning");
    return;
  }

  if (!userInSession || !userInSession.userId || !userInSession.phoneNumber) {
    showToast("Vui lòng đăng nhập để xác minh giao dịch", "error");
    return;
  }

  try {
    showLoading(true);
    const response = await fetch(
        "https://script.google.com/macros/s/AKfycbwTfULNVpgezDygzUXfdg-RFYAdbUAddmzlgvIRvdWzzwlvY7IROtuPjyfJLPfDIj6g/exec"
    );
    if (!response.ok) throw new Error("Không thể kiểm tra giao dịch");
    const data = await response.json();
    const finalRes = data.data;

    for (const bankData of finalRes) {
      if (bankData["Mã GD"] === transactionCode) {
        const paymentData = {
          amount: Number(bankData["Giá trị"]),
          transCode: transactionCode,
          userId: Number(userInSession.userId),
        };

        const check = await postPayment(paymentData);
        if (check.success) {
          paymentSuccess = true;
          stopPaymentCheck();
          document.getElementById("bankTransferContainer").classList.add("success");
          document.getElementById("bankTransferContainer").classList.remove("failed");
          document.getElementById("paymentTitle").classList.add("success");
          document.getElementById("paymentTitle").classList.remove("failed");
          document.getElementById("paymentTitle").textContent = "Thanh toán thành công!";
          showToast("Xác minh giao dịch thành công! Đang xử lý đơn hàng...", "success");
          const orderData = prepareOrderData();
          if (orderData) {
            submitOrder(orderData);
          }
          return;
        }
      }
    }
    showToast("Mã giao dịch không hợp lệ", "error");
  } catch (error) {
    console.error("Lỗi xác minh giao dịch:", error);
    showToast("Có lỗi khi xác minh giao dịch", "error");
  } finally {
    showLoading(false);
  }
}

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
        if (data.length !== 0) {
          cartData.items = data;
          renderCartItems(data);
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
              <img src="${item.imageFile}" alt="${item.productName}" 
                   onerror="this.src='/img/placeholder.svg'">
          </div>
          <div class="item-details">
              <h3 class="item-title">${item.productName}</h3>
              <p class="item-author">Tác giả: ${item.author}</p>
          </div>
          <div class="item-quantity">
              <div class="quantity-controls">
                  <button class="qty-btn" onclick="updateQuantity(${item.productId}, ${item.quantity}-1)">
                      <i class="ri-subtract-line"></i>
                  </button>
                  <input type="number" class="qty-input" value="${item.quantity}" min="1" max="10" 
                         onchange="updateQuantity(${item.productId}, this.value, true)">
                  <button class="qty-btn" onclick="updateQuantity(${item.productId}, ${item.quantity}+1)">
                      <i class="ri-add-line"></i>
                  </button>
              </div>
          </div>
          <div class="item-total">
               <span class="total-price">
                 ${formatCurrency(parseFloat((parseFloat(item.price) * (100 - item.discount)) / 100 * item.quantity))}
            </span>          
           </div>
          <div class="item-actions">
              <button class="action-btn remove-btn" onclick="removeItem(${item.productId})" 
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

async function updateQuantity(itemId, change, isAbsolute = false) {
  showLoading(true);

  const quantity = isAbsolute ? parseInt(change) : change;
  const available = cartData.items.find((s) => s.productId === itemId);

  const cartItem = {
    productId: itemId,
    quantity: quantity < 0 ? 0 : quantity,
  };

  if (available.available >= quantity) {
    await fetch("/cart/api/add", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Requested-With": "XMLHttpRequest",
      },
      body: JSON.stringify(cartItem),
    })
        .then((data) => {
          showLoading(false);
          loadCartItems();
          showToast("Đã cập nhật số lượng", "success");
          updateBankTransferQR();
        })
        .catch((error) => {
          showLoading(false);
          console.error("Error:", error);
          showToast("Có lỗi xảy ra khi cập nhật", "error");
        });
  } else {
    showToast("Vượt quá số lượng trong kho!", "error");
    showLoading(false);
  }
}

function removeItem(itemId) {
  showModal("Xác nhận xóa", "Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?", () => {
    const cartItem = {
      productId: itemId,
      quantity: 0,
    };

    showLoading(true);
    fetch(`/cart/api/delete`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Requested-With": "XMLHttpRequest",
      },
      body: JSON.stringify(cartItem),
    })
        .then(() => {
          showLoading(false);
          loadCartItems();
          showToast("Đã xóa sản phẩm khỏi giỏ hàng", "success");
          updateBankTransferQR();
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
    fetch("/cart/api/delete/allItems", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Requested-With": "XMLHttpRequest",
      },
    })
        .then(() => {
          showLoading(false);
          showEmptyCart();
          showToast("Đã xóa tất cả sản phẩm", "success");
          updateBankTransferQR();
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
  cartData.appliedPromo = null;
  document.getElementById("appliedPromos").innerHTML = "";
  calculateTotals();
  updateBankTransferQR();
}

function calculateTotals() {
  const subtotal = cartData.items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  let discount = calculateDiscount(subtotal);
  if (cartData.appliedPromo) {
    discount += Math.round(subtotal * cartData.appliedPromo.discount);
  }
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

function updateShippingFeeAndBill() {
  const shippingMethod = document.querySelector('input[name="shippingMethod"]:checked');
  cartData.shippingFee = parseInt(shippingMethod.value);
  calculateTotals();
  updateBankTransferQR();
}

function proceedToCheckout() {
  if (cartData.items.length === 0) {
    showToast("Giỏ hàng của bạn đang trống", "warning");
    return;
  }

  const shippingSection = document.getElementById("shippingSection");
  if (!shippingSection) {
    console.error("Phần thông tin giao hàng không tìm thấy");
    showToast("Có lỗi xảy ra", "error");
    return;
  }

  shippingSection.style.display = "block";
  shippingSection.classList.add("active");
  updateShippingFeeAndBill();
}

function backToSummary() {
  const shippingSection = document.getElementById("shippingSection");
  shippingSection.classList.remove("active");
  shippingSection.style.display = "none";
  cartData.shippingFee = 30000; // Reset to default
  calculateTotals();
  stopPaymentCheck();
  document.getElementById("bankTransferContainer").style.display = "none";
  document.getElementById("bankTransferContainer").classList.remove("active");
}

function updateBankTransferQR() {
  if (
      document.querySelector('input[name="paymentMethod"]:checked').value === "bank_transfer" &&
      document.getElementById("bankTransferContainer").classList.contains("active")
  ) {
    initializeBankTransfer();
  }
}

function prepareOrderData() {
  const form = document.getElementById("shippingForm");
  if (!form.checkValidity()) {
    form.reportValidity();
    return null;
  }

  const formData = new FormData(form);
  const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked')?.value;

  if (!paymentMethod) {
    showToast("Vui lòng chọn phương thức thanh toán", "warning");
    return null;
  }

  const orderData = {
    items: cartData.items,
    userId: userInSession.userId,
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
    payment: paymentMethod,
    totals: {
      subtotal: cartData.subtotal,
      discount: cartData.discount,
      tax: cartData.tax,
      shipping: cartData.shippingFee,
      total: cartData.total,
    },
    promoCode: cartData.appliedPromo ? cartData.appliedPromo.code : "",
  };

  if (paymentMethod === "bank_transfer") {
    orderData.transCode = lastTransCode;
  }

  return orderData;
}

function confirmOrder(event) {
  event.preventDefault();

  const orderData = prepareOrderData();
  if (!orderData) return;

  showModal(
      "Xác nhận đặt hàng",
      `Tổng tiền: ${formatCurrency(cartData.total)}<br>
     Phương thức thanh toán: ${orderData.payment === "cod" ? "Thanh toán khi nhận hàng" : "Chuyển khoản ngân hàng"}<br>
     ${orderData.promoCode ? `Mã giảm giá: ${orderData.promoCode}<br>` : ""}
     Bạn có chắc chắn muốn đặt hàng?`,
      () => {
        if (orderData.payment === "cod") {
          submitOrder(orderData);
        } else if (orderData.payment === "bank_transfer") {
          document.getElementById("bankTransferContainer").style.display = "block";
          document.getElementById("bankTransferContainer").classList.add("active");
          initializeBankTransfer();
        }
      }
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
      stopPaymentCheck();

      setTimeout(async function () {
        const deleteResponse = await fetch("/cart/api/delete/allItems", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "X-Requested-With": "XMLHttpRequest",
          },
        });

        if (!deleteResponse.ok) {
          throw new Error("Xóa giỏ hàng thất bại!");
        }

        const deleteResult = await deleteResponse.json();
        console.log("Xóa giỏ hàng:", deleteResult);
      }, 2000);

    } else {
      showToast(result.message || "Có lỗi xảy ra khi đặt hàng", "error");
    }

  } catch (error) {
    console.error("Lỗi khi gửi đơn hàng:", error);
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
  const loadingOverlay = document.getElementById("loadingOverlay");
  loadingOverlay.style.display = show ? "flex" : "none";
}