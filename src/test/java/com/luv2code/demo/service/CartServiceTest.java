package com.luv2code.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.ProductGetterDTO;
import com.luv2code.demo.dto.request.CartItemRequestDTO;
import com.luv2code.demo.dto.request.CartRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CartItemResponseDTO;
import com.luv2code.demo.entity.Cart;
import com.luv2code.demo.entity.CartItem;
import com.luv2code.demo.entity.Company;
import com.luv2code.demo.entity.Product;
import com.luv2code.demo.entity.User;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.exc.custom.QuantityNotAvailableException;
import com.luv2code.demo.repository.CartRepository;
import com.luv2code.demo.service.impl.CartService;
import com.luv2code.demo.service.impl.ProductService;
import com.luv2code.demo.service.impl.UserService;

class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String ERROR_PRODUCT_RETRIEVAL_FAILED = "Product retrieval failed";
    private static final String ERROR_DELETION_FAILED = "Deletion failed";
    private static final String ERROR_UPDATE_FAILED = "Update failed";
    private static final String ERROR_CARTITEM_NOT_FOUND = NotFoundTypeException.CARTITEM + " Not Found!";
    private static final String ERROR_QUANTITY_NOT_AVAILABLE = "Quantity Is Not Available For Product: Product";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(10.0);
    private static final int PRODUCT_QUANTITY = 10;
    private static final int CART_ITEM_QUANTITY = 5;
    private static final Long PRODUCT_ID = 1L;
    private static final Long CART_ITEM_ID = 1L;

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

    private Product createProduct(Long id, int quantity) {
        Product product = new Product();
        product.setId(id);
        product.setName("Product");
        product.setDescription("Description");
        product.setQuantity(quantity);
        product.setPrice(PRODUCT_PRICE);
        product.setCompany(new Company()); // Assuming Company is always needed
        return product;
    }

    private CartRequestDTO createCartRequestDTO(Long productId, int quantity) {
        return new CartRequestDTO(TEST_EMAIL, new CartItemRequestDTO(productId, quantity, PRODUCT_PRICE));
    }

    /**
     * Verifies that adding a cart item is successful by asserting the response
     * DTO values.
     *
     * @return void
     */
    @Test
    void shouldAddCartItemSuccessfully() {
        CartRequestDTO cartRequestDTO = createCartRequestDTO(PRODUCT_ID, CART_ITEM_QUANTITY);
        Product product = createProduct(PRODUCT_ID, PRODUCT_QUANTITY);
        product.getCompany().setName("CompanyName");

        Cart cart = new Cart();
        cart.setId(1L);

        when(userService.getUserSetterByEmail(cartRequestDTO.getEmail())).thenReturn(new User());
        when(productService.getProductCartSetter(cartRequestDTO.getCartItems().getProductId())).thenReturn(product);
        when(productService.updateProductQuantityById(anyLong(), anyInt())).thenReturn(1);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartItemResponseDTO responseDTO = cartService.addCartItem(cartRequestDTO);

        assertAll("Verify response DTO",
                () -> assertEquals(product.getId(), responseDTO.getProductId()),
                () -> assertEquals(product.getName(), responseDTO.getProductName()),
                () -> assertEquals(product.getDescription(), responseDTO.getProductDescription()),
                () -> assertEquals(product.getCompany().getName(), responseDTO.getProductCompanyName()),
                () -> assertEquals(cartRequestDTO.getCartItems().getQuantity(), responseDTO.getCartItemQuantity()),
                () -> assertEquals(PRODUCT_PRICE.add(new BigDecimal(40)), responseDTO.getCartItemPrice()),
                () -> assertEquals(cart.getId(), responseDTO.getCartId())
        );

        verify(productService).updateProductQuantityById(product.getId(), product.getQuantity() - cartRequestDTO.getCartItems().getQuantity());
        verify(cartRepository).save(any(Cart.class));
    }

    /**
     * Verifies that adding a cart item throws a QuantityNotAvailableException
     * when the requested quantity exceeds the available stock.
     *
     * @return void
     */
    @Test
    void shouldThrowQuantityNotAvailableExceptionWhenQuantityExceedsAvailableStock() {
        CartRequestDTO cartRequestDTO = createCartRequestDTO(PRODUCT_ID, PRODUCT_QUANTITY + 5);
        Product product = createProduct(PRODUCT_ID, PRODUCT_QUANTITY);
        when(productService.getProductCartSetter(cartRequestDTO.getCartItems().getProductId())).thenReturn(product);

        QuantityNotAvailableException thrown = assertThrows(QuantityNotAvailableException.class, () -> cartService.addCartItem(cartRequestDTO));
        assertEquals(ERROR_QUANTITY_NOT_AVAILABLE, thrown.getMessage());

        verify(productService, never()).updateProductQuantityById(anyLong(), anyInt());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    /**
     * Verifies that the cart service handles exceptions properly when the
     * product service fails.
     *
     * @return void
     */
    @Test
    void shouldHandleExceptionWhenProductServiceFails() {
        CartRequestDTO cartRequestDTO = createCartRequestDTO(PRODUCT_ID, CART_ITEM_QUANTITY);
        when(productService.getProductCartSetter(cartRequestDTO.getCartItems().getProductId())).thenThrow(new RuntimeException(ERROR_PRODUCT_RETRIEVAL_FAILED));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> cartService.addCartItem(cartRequestDTO));
        assertEquals(ERROR_PRODUCT_RETRIEVAL_FAILED, thrown.getMessage());

        verify(productService).getProductCartSetter(cartRequestDTO.getCartItems().getProductId());
        verify(productService, never()).updateProductQuantityById(anyLong(), anyInt());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    /**
     * Verifies that deleting a cart item is successful.
     *
     * @return void
     */
    @Test
    void shouldDeleteCartItemSuccessfully() {
        Cart cart = new Cart();
        cart.setCartItem(new CartItem());

        when(cartRepository.findById(CART_ITEM_ID)).thenReturn(Optional.of(cart));
        when(cartRepository.updateProductQuantity(anyLong(), anyInt())).thenReturn(1);
        doNothing().when(cartRepository).delete(cart);

        ResponseEntity<ApiResponseDTO> response = cartService.deleteCartItem(CART_ITEM_ID);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Success Deleted For Item", response.getBody().getMessage());

        verify(cartRepository).findById(CART_ITEM_ID);
        verify(cartRepository).updateProductQuantity(CART_ITEM_ID, cart.getCartItem().getQuantity());
        verify(cartRepository).delete(cart);
    }

    /**
     * Verifies that a NotFoundException is thrown when attempting to delete a
     * cart item that does not exist.
     *
     * @return void
     */
    @Test
    void shouldThrowNotFoundExceptionWhenCartItemNotFoundWhenDeletingCart() {
        when(cartRepository.findById(CART_ITEM_ID)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> cartService.deleteCartItem(CART_ITEM_ID));
        assertEquals(ERROR_CARTITEM_NOT_FOUND, thrown.getMessage());

        verify(cartRepository).findById(CART_ITEM_ID);
        verify(cartRepository, never()).updateProductQuantity(anyLong(), anyInt());
        verify(cartRepository, never()).delete(any());
    }

    /**
     * Verifies that a RuntimeException is thrown when attempting to delete a
     * cart item and the deletion operation fails.
     *
     * @return void
     */
    @Test
    void shouldHandleExceptionWhenDeletingCartItemFails() {
        Cart cart = new Cart();
        cart.setCartItem(new CartItem());

        when(cartRepository.findById(CART_ITEM_ID)).thenReturn(Optional.of(cart));
        doThrow(new RuntimeException(ERROR_DELETION_FAILED)).when(cartRepository).delete(cart);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> cartService.deleteCartItem(CART_ITEM_ID));
        assertEquals(ERROR_DELETION_FAILED, thrown.getMessage());

        verify(cartRepository).findById(CART_ITEM_ID);
        verify(cartRepository).updateProductQuantity(CART_ITEM_ID, cart.getCartItem().getQuantity());
        verify(cartRepository).delete(cart);
    }

    /**
     * Test case for updating the quantity of a cart item successfully.
     *
     * @return void
     */
    @Test
    void shouldUpdateCartItemQuantitySuccessfully() {

        Integer newQuantity = 5;
        ProductGetterDTO productDTO = new ProductGetterDTO(PRODUCT_ID, "Product", 8, 3);

        when(cartRepository.findProductGetterDTO(PRODUCT_ID)).thenReturn(Optional.of(productDTO));
        when(cartRepository.updateCartItemQuantity(PRODUCT_ID, newQuantity)).thenReturn(1);

        Integer updatedQuantity = (productDTO.getQuantity() + productDTO.getCartItemQuantity()) - newQuantity;

        when(productService.updateProductQuantityById(PRODUCT_ID, updatedQuantity)).thenReturn(1);

        ResponseEntity<Map<String, Integer>> response = cartService.updateCartItem(newQuantity, PRODUCT_ID);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(newQuantity, response.getBody().get("quantity"));

        verify(cartRepository).findProductGetterDTO(PRODUCT_ID);
        verify(cartRepository).updateCartItemQuantity(PRODUCT_ID, newQuantity);
        verify(productService).updateProductQuantityById(PRODUCT_ID, updatedQuantity);

    }

    /**
     * Verifies that updating a cart item throws a QuantityNotAvailableException
     * when the requested quantity exceeds the available quantity.
     *
     * @return void
     */
    @Test
    void shouldThrowQuantityNotAvailableExceptionWhenRequestedQuantityExceedsAvailableQuantity() {
        Integer newQuantity = 15;
        ProductGetterDTO productDTO = new ProductGetterDTO(PRODUCT_ID, "Product", 10, 3);

        when(cartRepository.findProductGetterDTO(PRODUCT_ID)).thenReturn(Optional.of(productDTO));

        QuantityNotAvailableException thrown = assertThrows(QuantityNotAvailableException.class, () -> cartService.updateCartItem(newQuantity, PRODUCT_ID));
        assertEquals(ERROR_QUANTITY_NOT_AVAILABLE, thrown.getMessage());

        verify(cartRepository).findProductGetterDTO(PRODUCT_ID);
        verify(cartRepository, never()).updateCartItemQuantity(anyLong(), anyInt());
        verify(productService, never()).updateProductQuantityById(anyLong(), anyInt());
    }

    /**
     * Verifies that updating a cart item handles exceptions when updating the
     * cart item quantity fails.
     *
     * @return void
     */
    @Test
    void shouldHandleExceptionWhenUpdatingCartItemQuantityFails() {
        Integer newQuantity = 5;
        ProductGetterDTO productDTO = new ProductGetterDTO(PRODUCT_ID, "Product", 10, 3);

        when(cartRepository.findProductGetterDTO(PRODUCT_ID)).thenReturn(Optional.of(productDTO));
        doThrow(new RuntimeException(ERROR_UPDATE_FAILED)).when(cartRepository).updateCartItemQuantity(PRODUCT_ID, newQuantity);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> cartService.updateCartItem(newQuantity, PRODUCT_ID));
        assertEquals(ERROR_UPDATE_FAILED, thrown.getMessage());

        verify(cartRepository).findProductGetterDTO(PRODUCT_ID);
        verify(cartRepository).updateCartItemQuantity(PRODUCT_ID, newQuantity);
        verify(productService, never()).updateProductQuantityById(anyLong(), anyInt());
    }

    /**
     * Verifies that retrieving all cart items for a user email is successful.
     *
     * @return void
     */
    @Test
    void shouldGetAllCartItemsForUserEmailSuccessfully() {
        CartItemResponseDTO item1 = new CartItemResponseDTO(1L, "Product1", "Description1", "Company1", 2, BigDecimal.valueOf(10.0), 101L);
        CartItemResponseDTO item2 = new CartItemResponseDTO(2L, "Product2", "Description2", "Company2", 1, BigDecimal.valueOf(20.0), 102L);
        List<CartItemResponseDTO> cartItems = List.of(item1, item2);

        when(cartRepository.findAllCartItemsWithUserEmail(TEST_EMAIL)).thenReturn(cartItems);

        ResponseEntity<Map<String, Object>> response = cartService.getAllCartItemsForUserEmail(TEST_EMAIL);

        assertAll("Verify response",
                () -> assertEquals(200, response.getStatusCode().value()),
                () -> assertEquals(BigDecimal.valueOf(30.0), response.getBody().get("total_price")),
                () -> assertEquals(cartItems, response.getBody().get("cartItems"))
        );
    }

    /**
     * Verifies that the cart service handles an empty cart correctly.
     *
     * @return void
     */
    @Test
    void shouldHandleEmptyCart() {
        when(cartRepository.findAllCartItemsWithUserEmail(TEST_EMAIL)).thenReturn(List.of());

        ResponseEntity<Map<String, Object>> response = cartService.getAllCartItemsForUserEmail(TEST_EMAIL);

        assertAll("Verify response",
                () -> assertEquals(200, response.getStatusCode().value()),
                () -> assertEquals(BigDecimal.ZERO, response.getBody().get("total_price")),
                () -> assertEquals(List.of(), response.getBody().get("cartItems"))
        );
    }

    /**
     * Verifies that the cart service handles exceptions thrown by the
     * repository correctly.
     *
     * @return void
     */
    @Test
    void shouldHandleExceptionFromRepository() {
        when(cartRepository.findAllCartItemsWithUserEmail(anyString())).thenThrow(new RuntimeException("Database error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> cartService.getAllCartItemsForUserEmail(TEST_EMAIL));
        assertEquals("Database error", thrown.getMessage());
    }

    /**
     * Test case to verify that the cart service handles a null user email
     * correctly.
     *
     * @return void
     */
    @Test
    void shouldHandleNullUserEmail() {
        try {
            cartService.getAllCartItemsForUserEmail(null);
        } catch (IllegalArgumentException e) {
            assertEquals("User email cannot be null", e.getMessage());
        }
    }

}
