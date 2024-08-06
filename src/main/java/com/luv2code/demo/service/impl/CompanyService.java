package com.luv2code.demo.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.CompanyRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CompanyResponseDTO;
import com.luv2code.demo.entity.Company;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.helper.IFileHelper;
import com.luv2code.demo.repository.CompanyRepository;
import com.luv2code.demo.service.ICompanyService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class CompanyService implements ICompanyService {

    private final CompanyRepository companyRepository;
    private final IFileHelper fileHelper;
    private final SystemMapper mapper;

    @Override
    public List<CompanyResponseDTO> getAllCompanies() {

        log.info("Fetching all companies");

        return companyRepository.findAllCompanies();

    }

    @Override
    public ResponseEntity<ApiResponseDTO> deleteCompany(String name) throws IOException {

        log.info("Attempting to delete company with name: {}", name);

        Optional<Company> company = companyRepository.findCompanyWithProductsByName(name);

        if (company.isEmpty()) {
            log.warn("Company with name: {} not found", name);
            throw new NotFoundException(NotFoundTypeException.COMPANY + " Not Found!");
        }

        log.info("Deleting image from file system for company: {}", name);
        fileHelper.deleteImageFromFileSystem(company.get().getImageUrl());

        log.info("Deleting company from repository: {}", name);
        companyRepository.delete(company.get());

        return ResponseEntity.ok(new ApiResponseDTO("Success Delete Company."));

    }

    @Override
    public CompanyResponseDTO createCompany(CompanyRequestDTO companyRequestDTO)
            throws IllegalStateException, IOException {

        log.info("Creating new company with name: {}", companyRequestDTO.getName());

        String imageUrl = fileHelper.uploadFileToFileSystem(companyRequestDTO.getImage());

        Company company = mapper.companyRequestDTOTOCompany(companyRequestDTO);

        company.setImageUrl(imageUrl);

        CompanyResponseDTO companyDto = mapper.companyTOCompanyResponseDTO(companyRepository.save(company));

        log.info("Company created successfully with name: {}", companyDto.getName());

        return companyDto;

    }

    @Override
    public CompanyResponseDTO updateCompany(String name, CompanyRequestDTO companyRequestDTO)
            throws IllegalStateException, IOException {

        log.info("Updating company with name: {}", name);

        Optional<Company> company = companyRepository.findByName(name);

        if (company.isEmpty()) {
            log.warn("Company with name: {} not found", name);
            throw new NotFoundException(NotFoundTypeException.COMPANY + " Not Found!");
        }

        if (companyRequestDTO.getName() != null) {
            log.info("Updating company name to: {}", companyRequestDTO.getName());
            company.get().setName(companyRequestDTO.getName());
        }

        if (companyRequestDTO.getImage() != null) {

            log.info("Updating company image for: {}", name);

            fileHelper.deleteImageFromFileSystem(company.get().getImageUrl());

            String imageUrl = fileHelper.uploadFileToFileSystem(companyRequestDTO.getImage());

            company.get().setImageUrl(imageUrl);

        }

        log.info("Company updated successfully with ID: {}", company.get().getId());

        return mapper.companyTOCompanyResponseDTO(companyRepository.save(company.get()));

    }

    @Override
    public Company getCompanySetter(String name) {

        log.info("Fetching company setter for name: {}", name);

        Optional<Company> company = Optional.ofNullable(mapper.companySetterDTOTOCompany(companyRepository.findCompanySetterDTOByName(name).get()));

        if (company.isEmpty()) {
            log.warn("Company with name: {} not found", name);
            throw new NotFoundException(NotFoundTypeException.COMPANY + " Not Found!");
        }

        log.info("Company found with ID: {}", company.get().getId());

        return company.get();

    }

}
