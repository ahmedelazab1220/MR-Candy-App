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
import com.luv2code.demo.repository.CartItemRepository;
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
	private final CartItemRepository cartItemRepository;
	private final IUserService userService;
	private final IProductService productService;

	@Transactional
	@Override
	public CartItemResponseDTO addCartItem(CartRequestDTO cartRequestDTO) {

		CartItemRequestDTO cartItemDTO = cartRequestDTO.getCartItems();

		Cart cart = new Cart();

		cart.setUser(userService.getUserSetterByEmail(cartRequestDTO.getEmail()));

		CartItem cartItem = new CartItem();

		Product product = productService.getProductCartSetter(cartItemDTO.getProductId());

		if (cartItemDTO.getQuantity() > product.getQuantity()) {
			log.warn("Quantity not available for product: {}", product.getName());
			throw new QuantityNotAvailableException("Quantity Is Not Available For Product : " + product.getName());
		}

		log.info("Product: {} - Reducing quantity from {} to {}", product.getName(), product.getQuantity(),
				product.getQuantity() - cartItemDTO.getQuantity());

		productService.updateProductQuantityById(product.getId(), product.getQuantity() - cartItemDTO.getQuantity());

		cartItem.setCart(cart);
		cartItem.setProduct(product);
		cartItem.setPrice(cartItemDTO.getPrice());
		cartItem.setQuantity(cartItemDTO.getQuantity());

		cart.setCartItem(cartItem);

		Cart savedCart = cartRepository.save(cart);

		return new CartItemResponseDTO(product.getId(), product.getName(), product.getDescription(),
				product.getCompany().getName(), cartItem.getQuantity(), cartItem.getPrice(), savedCart.getId());

	}

	@Transactional
	@Override
	public ResponseEntity<ApiResponseDTO> deleteCartItem(Long theId) {

		Optional<Cart> cart = cartRepository.findById(theId);

		if (cart.isEmpty()) {
			throw new NotFoundException(NotFoundTypeException.CARTITEM + " Not Found!");
		}

		cartRepository.updateProductQuantity(theId, cart.get().getCartItem().getQuantity());

		cartRepository.delete(cart.get());

		return ResponseEntity.ok(new ApiResponseDTO("Success Deleted For Item"));

	}

	@Transactional
	@Override
	public ResponseEntity<Map<String, Integer>> updateCartItem(Integer newQuantity, Long theId) {

		Optional<ProductGetterDTO> productGetterDTO = cartRepository.findProductGetterDTO(theId);

		if (productGetterDTO.isEmpty()) {
			throw new NotFoundException(NotFoundTypeException.CARTITEM + " Not Found!");
		}

		Long productId = productGetterDTO.get().getId();
		Integer productQuantity = productGetterDTO.get().getQuantity();
		String productName = productGetterDTO.get().getName();

		Integer cartQuantity = productGetterDTO.get().getCartItemQuantity();
		productQuantity += cartQuantity;

		if (newQuantity > productQuantity) {
			log.warn("Quantity not available for product: {}", productName);
			throw new QuantityNotAvailableException("Quantity Is Not Available For Product : " + productName);
		}

		cartItemRepository.updateCartItemQuantity(newQuantity, theId);

		log.info("Product: {} - Updating quantity from {} to {}", productName, productQuantity,
				productQuantity - newQuantity);

		productService.updateProductQuantityById(productId, productQuantity - newQuantity);

		return ResponseEntity.ok(Map.of("qunatity", newQuantity));

	}

	@Override
	public ResponseEntity<Map<String, Object>> getAllCartItems(Long userId) {

		List<CartItemResponseDTO> cartItemResponseDTO = cartRepository.findAllCartItemsWithUserID(userId);

		BigDecimal totalPrice = new BigDecimal(0);

		for (CartItemResponseDTO tmp : cartItemResponseDTO) {
			totalPrice = totalPrice.add(tmp.getPrice());
		}

		return ResponseEntity.ok(Map.of("total_price", totalPrice, "cartItems", cartItemResponseDTO));

	}

}
