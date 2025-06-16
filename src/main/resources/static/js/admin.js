// Dữ liệu mô phỏng
const dashboardData = {
  revenue: 1500000,
  orders: 23,
  topBook: "Đắc Nhân Tâm",
  users: 5,
  books: [
    {
      name: "Nhà Giả Kim",
      author: "Paulo Coelho",
      price: "120,000₫",
      date: "2025-05-28",
      img: "https://picsum.photos/50/70?random=1"
    },
    {
      name: "Tuổi Trẻ Đáng Giá Bao Nhiêu",
      author: "Rosie Nguyễn",
      price: "98,000₫",
      date: "2025-05-25",
      img: "https://picsum.photos/50/70?random=2"
    },
    {
      name: "Muôn Kiếp Nhân Sinh",
      author: "Nguyên Phong",
      price: "135,000₫",
      date: "2025-05-20",
      img: "https://picsum.photos/50/70?random=3"
    }
  ]
};

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("revenue").textContent = `${dashboardData.revenue.toLocaleString()}₫`;
  document.getElementById("orders").textContent = dashboardData.orders;
  document.getElementById("top-book").textContent = dashboardData.topBook;
  document.getElementById("users").textContent = dashboardData.users;

  const tableBody = document.getElementById("book-table-body");
  dashboardData.books.forEach(book => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td><img src="${book.img}" alt="${book.name}"></td>
      <td>${book.name}</td>
      <td>${book.author}</td>
      <td>${book.price}</td>
      <td>${book.date}</td>
    `;
    tableBody.appendChild(row);
  });
});


// Doanh thu 7 ngày qua (line chart)
const revenueCtx = document.getElementById('revenueChart').getContext('2d');
new Chart(revenueCtx, {
  type: 'line',
  data: {
    labels: ['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'],
    datasets: [{
      label: 'Doanh thu (₫)',
      data: [500000, 700000, 300000, 900000, 450000, 800000, 1000000],
      fill: true,
      backgroundColor: 'rgba(59, 130, 246, 0.2)',
      borderColor: '#3b82f6',
      tension: 0.3
    }]
  },
  options: {
    responsive: true,
    plugins: {
      legend: {
        display: false
      }
    }
  }
});

// Phân loại đơn hàng (pie chart)
const orderPieCtx = document.getElementById('orderPieChart').getContext('2d');
new Chart(orderPieCtx, {
  type: 'doughnut',
  data: {
    labels: ['Đã giao', 'Đang xử lý', 'Đã huỷ'],
    datasets: [{
      data: [65, 25, 10],
      backgroundColor: ['#10b981', '#f59e0b', '#ef4444'],
      hoverOffset: 8
    }]
  },
  options: {
    responsive: true,
    plugins: {
      legend: {
        position: 'bottom'
      }
    }
  }
});
