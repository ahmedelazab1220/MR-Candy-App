package com.luv2code.demo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.CompanyRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CompanyResponseDTO;
import com.luv2code.demo.entity.Company;

public interface ICompanyService {

    List<CompanyResponseDTO> getAllCompanies();

    ResponseEntity<ApiResponseDTO> deleteCompany(String name) throws IOException;

    CompanyResponseDTO createCompany(CompanyRequestDTO companyRequestDTO) throws IllegalStateException, IOException;

    CompanyResponseDTO updateCompany(String name, CompanyRequestDTO companyRequestDTO)
            throws IllegalStateException, IOException;

    Company getCompanySetter(String name);

}
