package com.luv2code.demo.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luv2code.demo.dto.ProductGetterDTO;
import com.luv2code.demo.dto.request.CartItemRequestDTO;
import com.luv2code.demo.dto.request.CartRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CartItemResponseDTO;
import com.luv2code.demo.entity.Cart;
import com.luv2code.demo.entity.CartItem;
import com.luv2code.demo.entity.Product;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.exc.custom.QuantityNotAvailableException;
import com.luv2code.demo.repository.CartRepository;
import com.luv2code.demo.service.ICartService;
import com.luv2code.demo.service.IProductService;
import com.luv2code.demo.service.IUserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final IUserService userService;
    private final IProductService productService;

    @Transactional
    @Override
    public CartItemResponseDTO addCartItem(CartRequestDTO cartRequestDTO) {

        CartItemRequestDTO cartItemDTO = cartRequestDTO.getCartItems();
        Cart cart = new Cart();
        cart.setUser(userService.getUserSetterByEmail(cartRequestDTO.getEmail()));

        Product product = productService.getProductCartSetter(cartItemDTO.getProductId());

        if (cartItemDTO.getQuantity() > product.getQuantity()) {
            log.warn("Quantity not available for product: {}", product.getName());
            throw new QuantityNotAvailableException("Quantity Is Not Available For Product: " + product.getName());
        }

        log.info("Product: {} - Reducing quantity from {} to {}", product.getName(), product.getQuantity(),
                product.getQuantity() - cartItemDTO.getQuantity());

        productService.updateProductQuantityById(product.getId(), product.getQuantity() - cartItemDTO.getQuantity());

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        BigDecimal cartItemPrice = cartItemDTO.getPrice().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity()));
        cartItem.setPrice(cartItemPrice);
        cartItem.setQuantity(cartItemDTO.getQuantity());

        cart.setCartItem(cartItem);

        Cart savedCart = cartRepository.save(cart);

        log.info("Cart item added successfully with ID: {}", savedCart.getId());

        return new CartItemResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCompany().getName(),
                cartItem.getQuantity(),
                cartItem.getPrice(),
                savedCart.getId()
        );
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponseDTO> deleteCartItem(Long theId) {

        Optional<Cart> cart = cartRepository.findById(theId);

        if (cart.isEmpty()) {
            log.error("Cart item with ID: {} not found", theId);
            throw new NotFoundException(NotFoundTypeException.CARTITEM + " Not Found!");
        }

        cartRepository.updateProductQuantity(theId, cart.get().getCartItem().getQuantity());
        cartRepository.delete(cart.get());

        log.info("Cart item with ID: {} deleted successfully", theId);

        return ResponseEntity.ok(new ApiResponseDTO("Success Deleted For Item"));
    }

    @Transactional
    @Override
    public ResponseEntity<Map<String, Integer>> updateCartItem(Integer newQuantity, Long theId) {

        Optional<ProductGetterDTO> productGetterDTO = cartRepository.findProductGetterDTO(theId);

        if (productGetterDTO.isEmpty()) {
            log.error("Cart item with ID: {} not found", theId);
            throw new NotFoundException(NotFoundTypeException.CARTITEM + " Not Found!");
        }

        ProductGetterDTO dto = productGetterDTO.get();
        Long productId = dto.getId();
        Integer productQuantity = dto.getQuantity();
        String productName = dto.getName();
        Integer cartQuantity = dto.getCartItemQuantity();
        productQuantity += cartQuantity;

        if (newQuantity > productQuantity) {
            log.warn("Requested quantity {} exceeds available quantity for product: {}", newQuantity, productName);
            throw new QuantityNotAvailableException("Quantity Is Not Available For Product: " + productName);
        }

        cartRepository.updateCartItemQuantity(theId, newQuantity);

        log.info("Product: {} - Updating quantity from {} to {}", productName, productQuantity, productQuantity - newQuantity);

        productService.updateProductQuantityById(productId, productQuantity - newQuantity);

        return ResponseEntity.ok(Map.of("quantity", newQuantity));
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllCartItemsForUserEmail(String userEmail) {

        List<CartItemResponseDTO> cartItemResponseDTO = cartRepository.findAllCartItemsWithUserEmail(userEmail);

        BigDecimal totalPrice = cartItemResponseDTO.stream()
                .map(CartItemResponseDTO::getCartItemPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Retrieved cart items for user email: {}. Total price: {}", userEmail, totalPrice);

        return ResponseEntity.ok(Map.of("total_price", totalPrice, "cartItems", cartItemResponseDTO));
    }
}
