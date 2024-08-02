package com.luv2code.demo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.luv2code.demo.dto.request.CompanyRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CompanyResponseDTO;

public interface ICompanyService {

	List<CompanyResponseDTO> getAllCompanies();

	ResponseEntity<ApiResponseDTO> deleteCompany(String name) throws IOException;

	CompanyResponseDTO createCompany(CompanyRequestDTO companyRequestDTO) throws IllegalStateException, IOException;

	Boolean existCompanyByName(String name);

	CompanyResponseDTO updateCompany(String name, CompanyRequestDTO companyRequestDTO)
			throws IllegalStateException, IOException;
	
}
