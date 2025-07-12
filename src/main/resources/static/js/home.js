//New Arrival
async function loadNewArrivals() {
    try {
        const response = await fetch('/api/product/new-arrivals');
        const products = await response.json();

        const container = document.getElementById('new-arrivals-content');
        if (!products || products.length === 0) {
            container.innerHTML = '<p class="text-center text-muted">No new books at the moment.</p>';
            return;
        }

        let html = '<div class="row g-4">';
        for (const product of products) {
            html += `
                <div class="col-lg-3 col-md-6">
                    <div class="book-card bg-white rounded shadow-sm">
                        <img src="${product.imageFile.startsWith('http') ? product.imageFile : '/images/' + product.imageFile}"
                             class="card-img-top" alt="Book Cover"/>
                        <div class="card-body">
                            <p class="text-primary mb-1">Book</p>
                            <h5 class="card-title">${product.productName}</h5>
                            <p class="card-text text-muted">${product.description ?? ''}</p>
                            <div class="d-flex justify-content-between align-items-center">
                                <span class="h5 mb-0">$${product.price}</span>
                                <a href="/product/${product.productId}" class="btn btn-outline-primary">View Details</a>
                            </div>
                        </div>
                    </div>
                </div>`;
        }
        html += '</div>';
        container.innerHTML = html;
    } catch (err) {
        console.error(err);
        document.getElementById('new-arrivals-content').innerHTML =
            '<p class="text-danger text-center">Failed to load new arrivals.</p>';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadNewArrivals();
});

//Top sale
document.addEventListener('DOMContentLoaded', function () {
    loadNewArrivals();
    loadTopSales();
});

function loadTopSales() {
    fetch('/api/product/top-sale')
        .then(response => response.json())
        .then(data => renderBooksSection(data, 'top-sale-content'))
        .catch(error => console.error('Error loading top sale:', error));
}

function renderBooksSection(products, containerId) {
    const container = document.getElementById(containerId);
    if (!products || products.length === 0) {
        container.innerHTML = `<p class="text-center text-muted">No products available at the moment.</p>`;
        return;
    }

    let html = '<div class="row g-4">';
    products.forEach(product => {
        const imageSrc = product.imageFile.startsWith('http') ? product.imageFile : `/images/${product.imageFile}`;
        html += `
            <div class="col-lg-3 col-md-6">
                <div class="book-card bg-white rounded shadow-sm">
                    <img src="${imageSrc}" class="card-img-top" alt="Book Cover"/>
                    <div class="card-body d-flex flex-column justify-content-between" style="height: 180px;">
                        <p class="text-primary mb-1">Book</p>
                        <h5 class="card-title">${product.productName}</h5>
                        <p class="card-text text-muted">${truncateText(product.description, 100)}</p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="h5 mb-0">$${product.price}</span>
                            <a href="/product/${product.productId}" class="btn btn-outline-primary btn-sm">View Details</a>
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

//Combo
fetch("/api/product/combo-by-tag")
    .then(response => response.json())
    .then(data => {
        const comboContainer = document.getElementById("tag-combo-content");

        Object.entries(data).forEach(([tag, products]) => {
            const comboCard = document.createElement("div");
            comboCard.className = "col-12";
            comboCard.innerHTML = `
                <div class="combo-card">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <div class="combo-title">${tag}</div>
                        <a href="/category/${encodeURIComponent(tag)}" class="btn btn-sm btn-outline-primary">See all</a>
                    </div>
                    <div class="combo-books d-flex flex-wrap gap-3">
                        ${products.map(p => `
                            <div class="book-card card" style="width: 180px;">
                                <img src="${p.imageFile}" class="card-img-top" alt="${p.productName}" style="height: 200px; object-fit: cover;">
                                <div class="card-body p-2">
                                    <h6 class="card-title">${p.productName}</h6>
                                    <p class="card-text text-muted">${p.description}</p>
                                    <p class="card-text text-primary fw-bold">${p.price.toLocaleString()}₫</p>
                                </div>
                            </div>
                        `).join("")}
                    </div>
                </div>
            `;
            comboContainer.appendChild(comboCard);
        });
    });
