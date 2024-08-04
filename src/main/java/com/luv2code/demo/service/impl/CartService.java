package com.luv2code.demo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.CartRequestDTO;
import com.luv2code.demo.dto.request.OrderItemRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CartResponseDTO;
import com.luv2code.demo.entity.Cart;
import com.luv2code.demo.entity.OrderItem;
import com.luv2code.demo.entity.Product;
import com.luv2code.demo.exc.custom.QuantityNotAvailableException;
import com.luv2code.demo.repository.CartRepository;
import com.luv2code.demo.service.ICartService;
import com.luv2code.demo.service.IProductService;
import com.luv2code.demo.service.IUserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CartService implements ICartService {

	private final CartRepository cartRepository;
	private final IUserService userService;
	private final IProductService productService;
	private final SystemMapper mapper;
	
	@Override
	public ResponseEntity<CartResponseDTO> createOrder(CartRequestDTO cartRequestDTO) {
		
		Cart cart = mapper.cartRequestDTOTOCart(cartRequestDTO);

		cart.setUser(userService.getUserSetterByEmail(cartRequestDTO.getUser_email()));
		
		List<OrderItemRequestDTO> orderItemsDto = cartRequestDTO.getOrderItems();
		
		List<OrderItem> orders = new ArrayList<OrderItem>();

		for (OrderItemRequestDTO tmp : orderItemsDto) {
			OrderItem x = new OrderItem();
			Product product = productService.getProductCartSetter(tmp.getProduct_id());

			if (tmp.getQuantity() > product.getQuantity()) {
				throw new QuantityNotAvailableException("Quantity Is Not Available For Product : " + product.getName());
			}

			product.setQuantity(product.getQuantity() - tmp.getQuantity());

			x.setPrice(tmp.getPrice());
			x.setQuantity(tmp.getQuantity());
			x.setProduct(product);
			x.setCart(cart);
			orders.add(x);
		}
		
		cart.setOrderItems(orders);

		Cart savedCart = cartRepository.save(cart);

		return ResponseEntity.ok(new CartResponseDTO(savedCart.getId(), "Success Add Order"));
		
	}

	@Override
	public ResponseEntity<ApiResponseDTO> deleteOrder(Long theId) {
		
		cartRepository.deleteById(theId);

		return ResponseEntity.ok(new ApiResponseDTO("Success Delete Order"));
		
	}

}
