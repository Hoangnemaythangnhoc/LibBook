package com.example.libbook.service.impl;

import com.example.libbook.entity.Coupon;
import com.example.libbook.repository.CouponRepository;
import com.example.libbook.repository.ProductRepository;
import com.example.libbook.utils.Converter;
import com.example.libbook.dto.CartItemDTO;
import com.example.libbook.dto.OrderDataDTO;
import com.example.libbook.dto.ShippingFormDTO;
import com.example.libbook.dto.TotalDTO;
import com.example.libbook.entity.Order;
import com.example.libbook.repository.OrderDetailRepository;
import com.example.libbook.repository.OrderRepository;
import com.example.libbook.service.OrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ProductRepository productRepository;


    @Override
    public List<Order> getAllOrders() {
        System.out.println("OrderService: Fetching all orders");
        List<Order> orders = orderRepository.getAllOrders();
        System.out.println("OrderService: Found " + orders.size() + " orders");
        return orders;
    }

    @Override
    public Order getOrderById(Integer orderId) {
        System.out.println("OrderService: Fetching order with id: " + orderId);
        Order order = orderRepository.getOrderById(orderId);
        System.out.println("OrderService: Found order: " + (order != null ? order.getOrderId() : "null"));
        return order;
    }

    @Override
    public void updateOrderStatus(Integer orderId, Integer newStatusId) {
        System.out.println("OrderService: Updating status for order id: " + orderId + " to status: " + newStatusId);
        orderRepository.updateOrderStatus(orderId, newStatusId);
        System.out.println("OrderService: Updated status for order id: " + orderId);
    }

    @Override
    public boolean addOrder(Map<String, Object> orderData) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            OrderDataDTO orderDataDTO = new OrderDataDTO();
            orderDataDTO.setItems(mapper.convertValue(orderData.get("items"), new TypeReference<List<CartItemDTO>>() {}));
            orderDataDTO.setShipping(mapper.convertValue(orderData.get("shipping"), ShippingFormDTO.class));
            orderDataDTO.setTotals(mapper.convertValue(orderData.get("totals"), TotalDTO.class));
            orderDataDTO.setPayment((String) orderData.get("payment"));
            orderDataDTO.setUserID((Integer) orderData.get("userId"));
            orderDataDTO.setTransCode( String.valueOf(orderData.get("transCode")));
            String code = (String)orderData.get("promoCode");
            if (code.equals("")){
                orderDataDTO.setPromoCode(0);
            }else {
                Coupon coupon = couponRepository.findByCode(code);
                orderDataDTO.setPromoCode(coupon.getCouponId());
            }
            Converter converter = new Converter();
            Order order = converter.convertOrderDTOOrder(orderDataDTO);

            int orderID = orderRepository.addNewOrder(order);
            if (orderID <= 0) {
                System.err.println("Lỗi khi tạo order.");
                return false;
            }
            for (CartItemDTO cart : order.getCartItemDTOS()) {
                productRepository.updateQuantityProduct(Math.toIntExact(cart.getProductId()), cart.getQuantity());
            }

            boolean orderDetailCheck = orderDetailRepository.addNewOrderDetail(order, orderID);
            return orderDetailCheck;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean cancelOrder(Order order) {
        return orderRepository.cancelOrder(order);
    }
}