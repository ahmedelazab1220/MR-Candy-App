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

@Service
@AllArgsConstructor
public class CompanyService implements ICompanyService {

	private final CompanyRepository companyRepository;
	private final IFileHelper fileHelper;
	private final SystemMapper mapper;

	@Override
	public List<CompanyResponseDTO> getAllCompanies() {

		return companyRepository.findAllCompanies();

	}

	@Override
	public ResponseEntity<ApiResponseDTO> deleteCompany(String name) throws IOException {

		Optional<Company> company = companyRepository.findByName(name);

		if (company.isEmpty()) {
			throw new NotFoundException(NotFoundTypeException.COMPANY + " Not Found!");
		}

		fileHelper.deleteImageFromFileSystem(company.get().getImageUrl());

		companyRepository.delete(company.get());

		return ResponseEntity.ok(new ApiResponseDTO("Success Delete Company."));

	}

	@Override
	public CompanyResponseDTO createCompany(CompanyRequestDTO companyRequestDTO)
			throws IllegalStateException, IOException {

		String imageUrl = fileHelper.uploadFileToFileSystem(companyRequestDTO.getImage());

		Company company = mapper.companyRequestDTOTCompany(companyRequestDTO);

		company.setImageUrl(imageUrl);

		CompanyResponseDTO companyDto = mapper.companyTOCompanyResponseDTO(companyRepository.save(company));

		return companyDto;

	}

	@Override
	public Boolean existCompanyByName(String name) {

		Boolean companyExist = companyRepository.existsByName(name);

		if (!companyExist) {
			throw new NotFoundException(NotFoundTypeException.COMPANY + " Not Found!");
		}

		return companyExist;
	}

	@Override
	public CompanyResponseDTO updateCompany(String name, CompanyRequestDTO companyRequestDTO)
			throws IllegalStateException, IOException {

		Optional<Company> company = companyRepository.findByName(name);

		if (company.isEmpty()) {
			throw new NotFoundException(NotFoundTypeException.COMPANY + " Not Found!");
		}

		if (companyRequestDTO.getName() != null) {
			company.get().setName(companyRequestDTO.getName());
		}

		if (companyRequestDTO.getImage() != null) {

			fileHelper.deleteImageFromFileSystem(company.get().getImageUrl());

			String imageUrl = fileHelper.uploadFileToFileSystem(companyRequestDTO.getImage());

			company.get().setImageUrl(imageUrl);

		}

		return mapper.companyTOCompanyResponseDTO(companyRepository.save(company.get()));

	}

}
