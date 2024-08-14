/*package com.luv2code.demo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.CartRequestDTO;
import com.luv2code.demo.dto.request.CartItemRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CartResponseDTO;
import com.luv2code.demo.entity.Cart;
import com.luv2code.demo.entity.CartItem;
import com.luv2code.demo.entity.Product;
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
    private final SystemMapper mapper;

    @Transactional
    @Override
    public ResponseEntity<CartResponseDTO> createOrder(CartRequestDTO cartRequestDTO) {

        log.info("Starting order creation for user: {}", cartRequestDTO.getEmail());

        Cart cart = mapper.cartRequestDTOTOCart(cartRequestDTO);

        cart.setUser(userService.getUserSetterByEmail(cartRequestDTO.getEmail()));

        List<CartItemRequestDTO> orderItemsDto = cartRequestDTO.getOrderItems();

        List<CartItem> orders = new ArrayList<CartItem>();

        for (CartItemRequestDTO tmp : orderItemsDto) {
            CartItem x = new CartItem();
            Product product = productService.getProductCartSetter(tmp.getProductId());

            if (tmp.getQuantity() > product.getQuantity()) {
                log.warn("Quantity not available for product: {}", product.getName());
                throw new QuantityNotAvailableException("Quantity Is Not Available For Product : " + product.getName());
            }

            log.info("Product: {} - Reducing quantity from {} to {}", product.getName(), product.getQuantity(), product.getQuantity() - tmp.getQuantity());

            productService.updateProductQuantityById(product.getId(), product.getQuantity() - tmp.getQuantity());

            x.setPrice(tmp.getPrice());
            x.setQuantity(tmp.getQuantity());
            x.setProduct(product);
            x.setCart(cart);
            orders.add(x);
        }

        cart.setCartItems(orders);

        Cart savedCart = cartRepository.save(cart);

        log.info("Order created successfully with cart ID: {}", savedCart.getId());

        return ResponseEntity.ok(new CartResponseDTO(savedCart.getId(), "Success Add Order"));

    }

    @Override
    public ResponseEntity<ApiResponseDTO> deleteOrder(Long theId) {

        log.info("Attempting to delete order with ID: {}", theId);

        cartRepository.deleteById(theId);

        log.info("Order with ID: {} deleted successfully", theId);

        return ResponseEntity.ok(new ApiResponseDTO("Success Delete Order"));

    }

}
*/