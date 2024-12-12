package com.luv2code.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CartItemResponseDTO;
import com.luv2code.demo.entity.Order;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.custom.CalculationException;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.repository.CartRepository;
import com.luv2code.demo.repository.OrderItemRepository;
import com.luv2code.demo.repository.OrderRepository;
import com.luv2code.demo.service.impl.OrderService;
import com.luv2code.demo.service.impl.UserService;

public class OrderServiceTest {

    private static final String USER_EMAIL = "test@example.com";
    private static final Long ORDER_ID = 1L;
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal POSITIVE_AMOUNT = BigDecimal.valueOf(50);
    private static final BigDecimal NEGATIVE_AMOUNT = BigDecimal.valueOf(-75);

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    /**
     * Sets up the necessary mocks for the test class before each test method is
     * executed.
     *
     * @return none
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests if an order is created successfully with valid cart items and user.
     *
     * @return none
     */
    @Test
    void shouldCreateOrderSuccessfully() {
        List<CartItemResponseDTO> cartItems = createCartItems();

        User user = createUser();

        when(cartRepository.findAllCartItemsWithUserEmail(USER_EMAIL)).thenReturn(cartItems);
        when(userService.getUserSetterByEmail(USER_EMAIL)).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(ORDER_ID);
            order.setCreatedAt(LocalDateTime.now());
            return order;
        });

        ResponseEntity<Map<String, Object>> response = orderService.createOrder(USER_EMAIL);

        assertNotNull(response.getBody().get("orderId"), "orderId should not be null");
        assertNotNull(response.getBody().get("orderRequestedAt"), "orderRequestedAt should not be null");
        assertNotNull(response.getBody().get("OrderTotalPrice"), "OrderTotalPrice should not be null");
        assertNotNull(response.getBody().get("userOrders"), "userOrders should not be null");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(ORDER_ID, response.getBody().get("orderId"));
        assertEquals(BigDecimal.valueOf(300), response.getBody().get("OrderTotalPrice"));
        assertNotNull(response.getBody().get("orderRequestedAt"));
        assertEquals(2, ((List<?>) response.getBody().get("userOrders")).size());

        verify(cartRepository).findAllCartItemsWithUserEmail(USER_EMAIL);
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).deleteAll(anyList());
    }

    /**
     * Tests if a NotFoundException is thrown when no cart items are found for
     * the given user email.
     *
     * @return none
     */
    @Test
    void shouldThrowNotFoundExceptionWhenCartItemsNotFound() {
        when(cartRepository.findAllCartItemsWithUserEmail(USER_EMAIL)).thenReturn(Collections.emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> orderService.createOrder(USER_EMAIL));
        assertEquals("CARTITEMS Not Found!", exception.getMessage());

        verify(cartRepository).findAllCartItemsWithUserEmail(USER_EMAIL);
        verifyNoMoreInteractions(cartRepository);
        verifyNoInteractions(orderRepository);
    }

    /**
     * Tests if the order total price is calculated correctly based on the cart
     * items.
     *
     * @return none
     */
    @Test
    void shouldHandleOrderTotalPriceCalculationCorrectly() {
        List<CartItemResponseDTO> cartItems = List.of(
                new CartItemResponseDTO(1L, "Product 1", "Description 1", "Company A", 3, POSITIVE_AMOUNT, 101L),
                new CartItemResponseDTO(2L, "Product 2", "Description 2", "Company B", 2, BigDecimal.valueOf(75), 102L)
        );

        User user = createUser();

        when(cartRepository.findAllCartItemsWithUserEmail(USER_EMAIL)).thenReturn(cartItems);
        when(userService.getUserSetterByEmail(USER_EMAIL)).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(ORDER_ID);
            order.setCreatedAt(LocalDateTime.now());
            return order;
        });

        ResponseEntity<Map<String, Object>> response = orderService.createOrder(USER_EMAIL);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(BigDecimal.valueOf(125), response.getBody().get("OrderTotalPrice"));
        assertNotNull(response.getBody().get("orderRequestedAt"));
    }

    /**
     * Tests if the cart items are deleted after a successful order creation.
     *
     * @return none
     */
    @Test
    void shouldDeleteCartsAfterOrderCreation() {
        List<CartItemResponseDTO> cartItems = List.of(
                new CartItemResponseDTO(1L, "Product 1", "Description 1", "Company A", 3, POSITIVE_AMOUNT, 101L)
        );

        User user = createUser();

        when(cartRepository.findAllCartItemsWithUserEmail(USER_EMAIL)).thenReturn(cartItems);
        when(userService.getUserSetterByEmail(USER_EMAIL)).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(ORDER_ID);
            order.setCreatedAt(LocalDateTime.now());
            return order;
        });

        orderService.createOrder(USER_EMAIL);

        verify(cartRepository).deleteAll(anyList());
    }

    /**
     * Tests if a NotFoundException is thrown when the user is not found for the
     * given email.
     *
     * @return none
     */
    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        List<CartItemResponseDTO> cartItems = createCartItems();

        when(cartRepository.findAllCartItemsWithUserEmail(USER_EMAIL)).thenReturn(cartItems);
        when(userService.getUserSetterByEmail(USER_EMAIL)).thenThrow(new NotFoundException("USER Not Found!"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> orderService.createOrder(USER_EMAIL));
        assertEquals("USER Not Found!", exception.getMessage());

        verify(userService).getUserSetterByEmail(USER_EMAIL);
        verifyNoInteractions(orderRepository);
    }

    /**
     * Tests if the order service handles edge cases in price calculation
     * correctly.
     *
     * This test case checks if the order service throws a CalculationException
     * when the total price of the cart items is negative.
     *
     * @return none
     */
    @Test
    void shouldHandleEdgeCasesInPriceCalculation() {
        List<CartItemResponseDTO> cartItems = List.of(
                new CartItemResponseDTO(1L, "Product 1", "Description 1", "Company A", 3, ZERO, 101L),
                new CartItemResponseDTO(2L, "Product 2", "Description 2", "Company B", 2, NEGATIVE_AMOUNT, 102L)
        );

        User user = createUser();

        when(cartRepository.findAllCartItemsWithUserEmail(USER_EMAIL)).thenReturn(cartItems);
        when(userService.getUserSetterByEmail(USER_EMAIL)).thenReturn(user);

        CalculationException thrownException = assertThrows(CalculationException.class, () -> {
            orderService.createOrder(USER_EMAIL);
        });

        assertEquals("Total Price Is Negative, Please Make sure Of Product Price and retry!", thrownException.getMessage());

        verify(cartRepository).findAllCartItemsWithUserEmail(USER_EMAIL);
        verifyNoMoreInteractions(cartRepository);
        verifyNoInteractions(orderRepository);
    }

    /**
     * Tests if the order service handles failure when saving an order.
     *
     * This test case checks if the order service throws a RuntimeException when
     * the order repository fails to save an order.
     *
     * @return none
     */
    @Test
    void shouldHandleFailureWhenSavingOrder() {
        List<CartItemResponseDTO> cartItems = List.of(
                new CartItemResponseDTO(1L, "Product 1", "Description 1", "Company A", 3, POSITIVE_AMOUNT, 101L)
        );

        when(cartRepository.findAllCartItemsWithUserEmail(USER_EMAIL)).thenReturn(cartItems);

        User user = createUser();
        when(userService.getUserSetterByEmail(USER_EMAIL)).thenReturn(user);

        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.createOrder(USER_EMAIL));
        assertEquals("Database error", exception.getMessage());

        verify(orderRepository).save(any(Order.class));
    }

    /**
     * Tests if an order is deleted successfully.
     *
     * This test case checks if the order service deletes an order and its
     * associated order items successfully.
     *
     * @return none
     */
    @Test
    void shouldDeleteOrderSuccessfully() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(createOrder()));

        doNothing().when(orderItemRepository).deleteOrderItemsByOrderId(ORDER_ID);
        doNothing().when(orderRepository).deleteOrderById(ORDER_ID);

        ResponseEntity<ApiResponseDTO> response = orderService.deleteOrder(ORDER_ID);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Success Delete Order.", response.getBody().getMessage());

        verify(orderRepository).findById(ORDER_ID);
        verify(orderItemRepository).deleteOrderItemsByOrderId(ORDER_ID);
        verify(orderRepository).deleteOrderById(ORDER_ID);
    }

    /**
     * Tests if a NotFoundException is thrown when the order is not found.
     *
     * This test case checks if the order service throws a NotFoundException
     * when the order is not found in the database.
     *
     * @return none
     */
    @Test
    void shouldThrowNotFoundExceptionWhenOrderNotFound() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> orderService.deleteOrder(ORDER_ID));
        assertEquals(NotFoundTypeException.ORDER + " Not Found!", exception.getMessage());

        verify(orderRepository).findById(ORDER_ID);
        verifyNoMoreInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
    }

    /**
     * Creates a list of cart item response DTOs for testing purposes.
     *
     * @return a list of cart item response DTOs
     */
    private List<CartItemResponseDTO> createCartItems() {
        return List.of(
                new CartItemResponseDTO(1L, "Product 1", "Description 1", "Company A", 2, BigDecimal.valueOf(100), 101L),
                new CartItemResponseDTO(2L, "Product 2", "Description 2", "Company B", 1, BigDecimal.valueOf(200), 102L)
        );
    }

    /**
     * Creates a new User object with the provided email.
     *
     * @return a new User object with the provided email
     */
    private User createUser() {
        User user = new User();
        user.setEmail(USER_EMAIL);
        return user;
    }

    /**
     * Creates a new Order object with the provided id and current date and
     * time.
     *
     * @return a new Order object
     */
    private Order createOrder() {
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }
}
