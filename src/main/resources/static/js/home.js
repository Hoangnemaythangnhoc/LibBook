document.addEventListener('DOMContentLoaded', function () {
    loadTopSales();
    loadBooksByTag();
});

//Top sale
function loadTopSales() {
    fetch('/api/product/top-sale')
        .then(response => response.json())
        .then(data => renderTopSales(data))
        .catch(error => console.error('Lỗi khi tải sách bán chạy:', error));
}

function renderTopSales(products) {
    const container = document.getElementById('top-sale-content');
    if (!products || products.length === 0) {
        container.innerHTML = `<p class="text-center text-muted">Hiện chưa có sách bán chạy.</p>`;
        return;
    }

    let html = '<div class="row g-4">';
    products.filter(e => e.status !== 0).forEach((product, index) => {
        const imageSrc = product.imageFile.startsWith('http') ? product.imageFile : `/images/${product.imageFile}`;

        let badge = '';
        if (index === 0) badge = `<div class="top-badge">Top 1</div>`;
        else if (index === 1) badge = `<div class="top-badge">Top 2</div>`;
        else if (index === 2) badge = `<div class="top-badge">Top 3</div>`;

        // Giá khuyến mãi nếu có
        let priceHtml = '';
        if (product.discount > 0) {
            const discountedPrice = (product.price * (100 - product.discount)) / 100;
            priceHtml = `
                <div>
                    <span class="text-decoration-line-through text-danger me-1">${product.price.toLocaleString()}₫</span>
                    <span class="text-danger me-2">(-${product.discount}%)</span>
                    <span class="fw-bold">${discountedPrice.toLocaleString()}₫</span>
                </div>`;
        } else {
            priceHtml = `<span class="h5 mb-0">${product.price.toLocaleString()}₫</span>`;
        }

        html += `
            <div class="col-lg-3 col-md-6 position-relative">
                ${badge}
                <div class="book-card bg-white rounded shadow-sm">
                    <img src="${imageSrc}" class="card-img-top" alt="Bìa sách"/>
                    <div class="card-body d-flex flex-column justify-content-between" style="height: 180px;">
                        <p class="text-primary mb-1">Sách</p>
                        <h5 class="card-title">${product.productName}</h5>
                        <p class="card-text text-muted">${truncateText(product.description, 100)}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            ${priceHtml}
                            <a href="/product/${product.productId}" class="btn btn-outline-primary btn-sm">Chi tiết</a>
                        </div>
                    </div>
                </div>
            </div>
        `;
    });
    html += '</div>';
    container.innerHTML = html;
}

function truncateText(text, maxLength) {
    if (!text) return '';
    return text.length > maxLength ? text.slice(0, maxLength) + '...' : text;
}

//Category (sửa từ tag combo)
function loadBooksByTag() {
    fetch("/api/product/list-by-tag")
        .then(response => response.json())
        .then(data => {
            const comboContainer = document.getElementById("tag-combo-content");
            comboContainer.innerHTML = '';

            Object.entries(data).forEach(([tag, products]) => {
                let html = `
                    <div class="mb-5">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h4>${tag}</h4>
                            <a href="/category/${encodeURIComponent(tag)}" class="btn btn-sm btn-outline-primary">Xem tất cả</a>
                        </div>
                        <div class="row g-4">
                `;

                products.forEach(p => {
                    const imageSrc = p.imageFile.startsWith('http') ? p.imageFile : `/images/${p.imageFile}`;

                    let priceHtml = '';
                    if (p.discount > 0) {
                        const discountedPrice = (p.price * (100 - p.discount)) / 100;
                        priceHtml = `
                            <div>
                                <span class="text-decoration-line-through text-danger me-1">${p.price.toLocaleString()}₫</span>
                                <span class="text-danger me-2">(-${p.discount}%)</span>
                                <span class="fw-bold text-dark">${discountedPrice.toLocaleString()}₫</span>
                            </div>`;
                    } else {
                        priceHtml = `<span class="fw-bold text-primary">${p.price.toLocaleString()}₫</span>`;
                    }

                    html += `
                        <div class="col-lg-3 col-md-4 col-sm-6">
                            <div class="book-card bg-white rounded shadow-sm h-100">
                                <div class="image-wrapper" style="width:100%; height:200px; overflow:hidden; background-color:#f8f9fa; position:relative;">
                                    <img src="${imageSrc}" 
                                         alt="${p.productName}" 
                                         style="width:100%; height:100%; object-fit:cover;"
                                         onerror="this.onerror=null;this.src='/images/default-book.png';">
                                </div>

                                <div class="card-body p-2 d-flex flex-column justify-content-between" style="height: 200px;">
                                    <div>
                                        <h6 class="card-title">${p.productName}</h6>
                                        <p class="card-text text-muted">${truncateText(p.description, 80)}</p>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center mt-2">
                                        ${priceHtml}
                                        <a href="/product/${p.productId}" class="btn btn-outline-primary btn-sm">Chi tiết</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                });

                html += '</div></div>';
                comboContainer.innerHTML += html;
            });
        })
        .catch(err => console.error("Lỗi khi tải sách theo thể loại:", err));
}