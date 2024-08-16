package com.luv2code.demo.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.luv2code.demo.dto.request.CartItemRequestDTO;
import com.luv2code.demo.dto.request.CartRequestDTO;
import com.luv2code.demo.dto.response.CartItemResponseDTO;
import com.luv2code.demo.entity.Cart;
import com.luv2code.demo.entity.CartItem;
import com.luv2code.demo.entity.Product;
import com.luv2code.demo.exc.custom.QuantityNotAvailableException;
import com.luv2code.demo.repository.CartRepository;
import com.luv2code.demo.service.ICartService;
import com.luv2code.demo.service.IProductService;
import com.luv2code.demo.service.IUserService;

import jakarta.transaction.Transactional;
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
	public ResponseEntity<CartItemResponseDTO> addCartItem(CartRequestDTO cartRequestDTO) {

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

		return ResponseEntity.ok(new CartItemResponseDTO(product.getId(), product.getName(), product.getDescription(),
				product.getCompany().getName(), cartItem.getQuantity(), cartItem.getPrice(), savedCart.getId()));

	}

}
