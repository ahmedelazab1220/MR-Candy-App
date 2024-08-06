package com.luv2code.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.dto.request.CompanyRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CompanyResponseDTO;
import com.luv2code.demo.service.ICompanyService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${api.version}/companies")
@AllArgsConstructor
public class CompanyController {

    private final ICompanyService companyService;

    @GetMapping("")
    public List<CompanyResponseDTO> getAllCompanies() {

        return companyService.getAllCompanies();

    }

    @DeleteMapping("")
    public ResponseEntity<ApiResponseDTO> deleteCompany(@RequestParam(required = true) String name) throws IOException {

        return companyService.deleteCompany(name);

    }

    @PostMapping("")
    public CompanyResponseDTO createCompany(@Valid @ModelAttribute CompanyRequestDTO CompanyRequestDTO)
            throws IllegalStateException, IOException {

        return companyService.createCompany(CompanyRequestDTO);

    }

    @PutMapping("")
    public CompanyResponseDTO updateCompany(@RequestParam(required = true) String companyName,
            @Valid @ModelAttribute CompanyRequestDTO companyRequestDTO) throws IllegalStateException, IOException {

        return companyService.updateCompany(companyName, companyRequestDTO);

    }

}
