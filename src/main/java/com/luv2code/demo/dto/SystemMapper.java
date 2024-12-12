package com.luv2code.demo.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.luv2code.demo.dto.request.CartRequestDTO;
import com.luv2code.demo.dto.request.CategoryRequestDTO;
import com.luv2code.demo.dto.request.CompanyRequestDTO;
import com.luv2code.demo.dto.request.ProductRequestDTO;
import com.luv2code.demo.dto.request.RegisterRequestDTO;
import com.luv2code.demo.dto.request.UpdateUserProfileRequestDTO;
import com.luv2code.demo.dto.response.CategoryResponseDTO;
import com.luv2code.demo.dto.response.CompanyResponseDTO;
import com.luv2code.demo.dto.response.ProductDetailsResponseDTO;
import com.luv2code.demo.dto.response.UpdateUserProfileResponseDTO;
import com.luv2code.demo.dto.response.UserAuthenticationResponseDTO;
import com.luv2code.demo.dto.response.UserTokenResponseDTO;
import com.luv2code.demo.dto.setter.CategorySetterDTO;
import com.luv2code.demo.dto.setter.CompanySetterDTO;
import com.luv2code.demo.dto.setter.ProductCartSetterDTO;
import com.luv2code.demo.dto.setter.ProductSetterDTO;
import com.luv2code.demo.dto.setter.UserSetterDTO;
import com.luv2code.demo.entity.Cart;
import com.luv2code.demo.entity.Category;
import com.luv2code.demo.entity.Company;
import com.luv2code.demo.entity.Product;
import com.luv2code.demo.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SystemMapper {

    User userAuthenticationResponseDTOTOUser(UserAuthenticationResponseDTO userAuthenticationResponseDTO);

    User registerRequestDTOTOUser(RegisterRequestDTO registerRequestDTO);

    User userTokenResponseDTOTOUser(UserTokenResponseDTO userTokenResponseDTO);

    UserSetterDTO userTOUserSetterDTO(User user);

    User userSetterDTOTOUser(UserSetterDTO userSetterDTO);

    User updateUserProfileRequestTOUser(UpdateUserProfileRequestDTO updateUserProfileRequest, @MappingTarget User user);

    UpdateUserProfileResponseDTO updateUserProfileRequestTOUpdateUserProfileResponse(UpdateUserProfileRequestDTO updateUserProfileRequest);

    Category categoryRequestDTOTOCategory(CategoryRequestDTO categoryRequestDTO);

    CategoryResponseDTO categoryTOCategoryResponseDTO(Category category);

    Category categorySetterDTOTOcaCategory(CategorySetterDTO categorySetterDTO);

    Company companyRequestDTOTOCompany(CompanyRequestDTO companyRequestDTO);

    CompanyResponseDTO companyTOCompanyResponseDTO(Company company);

    Company companySetterDTOTOCompany(CompanySetterDTO companySetterDTO);

    Product productRequestDTOTOProduct(ProductRequestDTO productRequestDTO);

    ProductDetailsResponseDTO ProductTOproductDetailsResponseDTO(Product product);

    Product productSetterDTOTOProduct(ProductSetterDTO productSetterDTO);

    Product productCartSetterDTOTOProduct(ProductCartSetterDTO productSetterDTO);

    void updateProductFromRequestDTO(ProductRequestDTO productRequestDTO, @MappingTarget Product product);

    Cart cartRequestDTOTOCart(CartRequestDTO cartRequestDTO);

}
