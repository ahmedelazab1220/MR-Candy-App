package com.luv2code.demo.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CartItemResponseDTO;
import com.luv2code.demo.dto.response.OrderItemResponseDTO;
import com.luv2code.demo.entity.Cart;
import com.luv2code.demo.entity.Order;
import com.luv2code.demo.entity.OrderItem;
import com.luv2code.demo.entity.Product;
import com.luv2code.demo.exc.custom.CalculationException;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.repository.CartRepository;
import com.luv2code.demo.repository.OrderItemRepository;
import com.luv2code.demo.repository.OrderRepository;
import com.luv2code.demo.service.IOrderService;
import com.luv2code.demo.service.IUserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final IUserService userService;

    @Transactional
    @Override
    public ResponseEntity<Map<String, Object>> createOrder(String userEmail) {
        log.info("Entering createOrder method for userEmail: {}", userEmail);

        List<CartItemResponseDTO> cartItemResponseDTOs = cartRepository.findAllCartItemsWithUserEmail(userEmail);

        if (cartItemResponseDTOs.isEmpty()) {
            log.error("No cart items found for userEmail: {}", userEmail);
            throw new NotFoundException(NotFoundTypeException.CARTITEM + "S Not Found!");
        }

        Order order = new Order();
        List<OrderItemResponseDTO> orderItemResponseDTOs = new ArrayList<>();
        List<OrderItem> orderItems = processOrderItems(cartItemResponseDTOs, orderItemResponseDTOs, order);

        order.setOrderItems(orderItems);
        order.setTotalPrice(calculateOrderTotalPrice(orderItems));
        order.setUser(userService.getUserSetterByEmail(userEmail));

        if (order.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CalculationException("Total Price Is Negative, Please Make sure Of Product Price and retry!");
        }

        Order savedOrder = orderRepository.save(order);

        deleteCarts(cartItemResponseDTOs);

        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return ResponseEntity.ok(Map.of(
                "orderId", savedOrder.getId(),
                "orderRequestedAt", savedOrder.getCreatedAt(),
                "OrderTotalPrice", order.getTotalPrice(),
                "userOrders", orderItemResponseDTOs
        ));
    }

    private List<OrderItem> processOrderItems(List<CartItemResponseDTO> cartItems,
            List<OrderItemResponseDTO> orderItemResponseDTOs, Order order) {
        log.info("Processing order items");

        List<OrderItem> orderItems = cartItems.stream().map(cartItemResponseDTO -> {
            Product product = new Product();
            product.setId(cartItemResponseDTO.getProductId());
            product.setName(cartItemResponseDTO.getProductName());

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setPrice(cartItemResponseDTO.getCartItemPrice());
            orderItem.setQuantity(cartItemResponseDTO.getCartItemQuantity());
            orderItem.setOrder(order);

            OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO();
            orderItemResponseDTO.setOrderItemPrice(orderItem.getPrice());
            orderItemResponseDTO.setOrderItemQuantity(orderItem.getQuantity());
            orderItemResponseDTO.setProductName(cartItemResponseDTO.getProductName());
            orderItemResponseDTO.setProductCompanyName(cartItemResponseDTO.getProductCompanyName());

            orderItemResponseDTOs.add(orderItemResponseDTO);

            return orderItem;
        }).collect(Collectors.toList());

        log.info("Processed {} order items", orderItems.size());
        return orderItems;
    }

    private BigDecimal calculateOrderTotalPrice(List<OrderItem> orderItems) {
        log.info("Calculating order total price");

        BigDecimal totalPrice = orderItems.stream().map(OrderItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("Calculated total price: {}", totalPrice);
        return totalPrice;
    }

    private void deleteCarts(List<CartItemResponseDTO> cartItems) {
        log.info("Deleting carts for cartItem IDs: {}", cartItems.stream().map(CartItemResponseDTO::getCartId).collect(Collectors.toList()));

        List<Cart> cartsToDelete = cartRepository
                .findAllById(cartItems.stream().map(CartItemResponseDTO::getCartId).toList());

        cartRepository.deleteAll(cartsToDelete);
        log.info("Deleted {} carts", cartsToDelete.size());
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponseDTO> deleteOrder(Long orderId) {
        log.info("Entering deleteOrder method for orderId: {}", orderId);

        Optional<Order> order = orderRepository.findById(orderId);

        if (order.isEmpty()) {
            log.error("Order not found with ID: {}", orderId);
            throw new NotFoundException(NotFoundTypeException.ORDER + " Not Found!");
        }

        orderItemRepository.deleteOrderItemsByOrderId(orderId);
        orderRepository.deleteOrderById(order.get().getId());

        log.info("Order with ID: {} successfully deleted", orderId);
        return ResponseEntity.ok(new ApiResponseDTO("Success Delete Order."));
    }

}
