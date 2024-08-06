package com.luv2code.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.luv2code.demo.dto.SystemMapper;
import com.luv2code.demo.dto.request.CompanyRequestDTO;
import com.luv2code.demo.dto.response.ApiResponseDTO;
import com.luv2code.demo.dto.response.CompanyResponseDTO;
import com.luv2code.demo.entity.Company;
import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.NotFoundTypeException;
import com.luv2code.demo.helper.IFileHelper;
import com.luv2code.demo.repository.CompanyRepository;
import com.luv2code.demo.service.impl.CompanyService;

class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private IFileHelper fileHelper;

    @Mock
    private SystemMapper mapper;

    private Company company;

    private CompanyRequestDTO companyRequestDTO;

    private CompanyResponseDTO companyResponseDTO;

    private MultipartFile multipartFile;

    private String imageUrl;

    private String companyName;

    /**
     * Sets up the necessary mocks and initializes the role and user objects
     * before each test case.
     *
     * @throws Exception if there is an error with the mocks initialization.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        imageUrl = "http://example.com/image.png";
        companyName = "Company";

        company = new Company();
        company.setId(1L);
        company.setCreatedAt(LocalDateTime.now());
        company.setName(companyName);
        company.setImageUrl(imageUrl);

        multipartFile = new MockMultipartFile("image", "image.png", "image/png", "imageContent".getBytes());

    }

    CompanyRequestDTO getCompanyRequestDTO() {
        return new CompanyRequestDTO(companyName, multipartFile);
    }

    CompanyResponseDTO getCompanyResponseDTO() {
        return new CompanyResponseDTO(companyName, imageUrl);
    }

    /**
     * Test case to verify that the getAllCompanies method of the CompanyService
     * returns a list of CompanyResponseDTO objects successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void shouldGetAllCompaniesSuccessfully() {

        List<CompanyResponseDTO> CompanyResponseDTOs = List.of(new CompanyResponseDTO(companyName, imageUrl),
                new CompanyResponseDTO(companyName, imageUrl));

        when(companyRepository.findAllCompanies()).thenReturn(CompanyResponseDTOs);

        List<CompanyResponseDTO> result = companyService.getAllCompanies();

        assertEquals(CompanyResponseDTOs, result);

        verify(companyRepository, times(1)).findAllCompanies();

    }

    /**
     * Test case to verify that the getAllCompanies() method returns an empty
     * list when there are no Companies.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void shouldGetAllCompaniesWhenNoCompanies() {

        List<CompanyResponseDTO> Companies = List.of(); // Empty list

        when(companyRepository.findAllCompanies()).thenReturn(Companies);

        List<CompanyResponseDTO> result = companyService.getAllCompanies();

        assertTrue(result.isEmpty());

        verify(companyRepository, times(1)).findAllCompanies();

    }

    /**
     * Test case to verify that the createCompany method of the CompanyService
     * creates a Company successfully.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldCreateCompanySuccessfully() throws IOException {

        companyRequestDTO = getCompanyRequestDTO();
        companyResponseDTO = getCompanyResponseDTO();

        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class))).thenReturn(imageUrl);
        when(mapper.companyRequestDTOTOCompany(companyRequestDTO)).thenReturn(company);
        when(mapper.companyTOCompanyResponseDTO(any(Company.class))).thenReturn(companyResponseDTO);
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyResponseDTO result = companyService.createCompany(companyRequestDTO);

        assertEquals(companyResponseDTO.getName(), result.getName());
        assertEquals(companyResponseDTO.getImageUrl(), result.getImageUrl());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(mapper, times(1)).companyRequestDTOTOCompany(companyRequestDTO);
        verify(mapper, times(1)).companyTOCompanyResponseDTO(any(Company.class));
        verify(companyRepository, times(1)).save(any(Company.class));

    }

    /**
     * Test case to verify that an exception is thrown when creating a Company
     * with invalid data.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldThrowExceptionWhenCreatingCompanyWhenSentInvalidData() throws IOException {

        CompanyRequestDTO invalidRequest = new CompanyRequestDTO("", null);

        assertThrows(NullPointerException.class, () -> {
            companyService.createCompany(invalidRequest);
        });

        verify(fileHelper, times(0)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(companyRepository, times(0)).save(any(Company.class));
    }

    /**
     * Test case to verify that an exception is thrown when saving a Company.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldThrowExceptionWhenSaveCompany() throws IOException {

        companyRequestDTO = getCompanyRequestDTO();

        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class))).thenReturn(imageUrl);
        when(mapper.companyRequestDTOTOCompany(companyRequestDTO)).thenReturn(company);
        doThrow(new RuntimeException("Failed to save Company")).when(companyRepository).save(any(Company.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            companyService.createCompany(companyRequestDTO);
        });

        assertEquals("Failed to save Company", exception.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(companyRepository, times(1)).save(company);
        verify(mapper, times(1)).companyRequestDTOTOCompany(companyRequestDTO);
        verify(mapper, times(0)).companyTOCompanyResponseDTO(any(Company.class));

    }

    /**
     * Test case to verify that an exception is thrown when file upload fails
     * when creating a Company.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldThrowExceptionWhenFileUploadFailsWhenCreatingCompany() throws IOException {

        companyRequestDTO = getCompanyRequestDTO();

        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class)))
                .thenThrow(new IOException("File upload failed"));

        IOException exception = assertThrows(IOException.class, () -> {
            companyService.createCompany(companyRequestDTO);
        });

        assertEquals("File upload failed", exception.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(companyRepository, times(0)).save(any(Company.class));
        verify(mapper, times(0)).companyRequestDTOTOCompany(any(CompanyRequestDTO.class));
        verify(mapper, times(0)).companyTOCompanyResponseDTO(any(Company.class));

    }

    /**
     * Test case to verify that an exception is thrown when the mapping from
     * CompanyRequestDTO to Company fails during the creation of a Company.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldHandleCompanyRequestDTOTOCompanyFailureWhenCreatingCompany() throws IOException {

        CompanyRequestDTO CompanyRequestDTO = getCompanyRequestDTO();
        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class))).thenReturn(imageUrl);
        doThrow(new RuntimeException("Mapping to Company failed")).when(mapper)
                .companyRequestDTOTOCompany(CompanyRequestDTO);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            companyService.createCompany(CompanyRequestDTO);
        });

        assertEquals("Mapping to Company failed", thrown.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(mapper, times(1)).companyRequestDTOTOCompany(CompanyRequestDTO);
        verify(mapper, times(0)).companyTOCompanyResponseDTO(any(Company.class));
        verify(companyRepository, times(0)).save(any(Company.class));
    }

    /**
     * Test case to verify that an exception is thrown when the mapping from
     * Company to CompanyResponseDTO fails during the creation of a Company.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldHandleCompanyTOCompanyResponseDTOFailureWhenCreatingCompany() throws IOException {

        CompanyRequestDTO CompanyRequestDTO = getCompanyRequestDTO();

        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class))).thenReturn(imageUrl);
        when(mapper.companyRequestDTOTOCompany(CompanyRequestDTO)).thenReturn(company);
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        doThrow(new RuntimeException("Mapping to ResponseDTO failed")).when(mapper)
                .companyTOCompanyResponseDTO(company);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            companyService.createCompany(CompanyRequestDTO);
        });

        assertEquals("Mapping to ResponseDTO failed", thrown.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(mapper, times(1)).companyRequestDTOTOCompany(CompanyRequestDTO);
        verify(mapper, times(1)).companyTOCompanyResponseDTO(company);
        verify(companyRepository, times(1)).save(any(Company.class));

    }

    /**
     * Test case to verify that a NotFoundException is thrown when the Company
     * is not found when deleting a Company.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldThrowNotFoundExceptionWhenCompanyNotFoundWhenDeletingCompany() throws IOException {

        when(companyRepository.findCompanyWithProductsByName(companyName)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            companyService.deleteCompany(companyName);
        });

        assertEquals(NotFoundTypeException.COMPANY + " Not Found!", exception.getMessage());

        verify(companyRepository, times(1)).findCompanyWithProductsByName(companyName);
        verify(fileHelper, times(0)).deleteImageFromFileSystem(anyString());
        verify(companyRepository, times(0)).delete(any(Company.class));

    }

    /**
     * Test case to verify that the deleteCompany method handles image deletion
     * failure when deleting a Company.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldHandleImageDeletionFailureWhenDeletingCompany() throws IOException {

        when(companyRepository.findCompanyWithProductsByName(companyName)).thenReturn(Optional.of(company));
        doThrow(new IOException("Failed to delete image")).when(fileHelper).deleteImageFromFileSystem(company.getImageUrl());
        doNothing().when(companyRepository).delete(company);

        IOException exception = assertThrows(IOException.class, () -> {
            companyService.deleteCompany(companyName);
        });

        assertEquals("Failed to delete image", exception.getMessage());

        verify(companyRepository, times(1)).findCompanyWithProductsByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(companyRepository, times(0)).delete(company);

    }

    /**
     * This test method verifies that the deleteCompany method successfully
     * deletes a Company.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldDeleteCompanySuccessfully() throws IOException {

        when(companyRepository.findCompanyWithProductsByName(companyName)).thenReturn(Optional.of(company));
        when(fileHelper.deleteImageFromFileSystem(company.getImageUrl())).thenReturn(true);
        doNothing().when(companyRepository).delete(any(Company.class));

        ResponseEntity<ApiResponseDTO> response = companyService.deleteCompany(companyName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success Delete Company.", response.getBody().getMessage());

        verify(companyRepository, times(1)).findCompanyWithProductsByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(companyRepository, times(1)).delete(company);

    }

    /**
     * Test case to verify that the deleteCompany method handles exceptions
     * during deletion of a Company.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldHandleExceptionDuringDeleteWhenDeletingCompany() throws IOException {

        when(companyRepository.findCompanyWithProductsByName(companyName)).thenReturn(Optional.of(company));
        when(fileHelper.deleteImageFromFileSystem(company.getImageUrl())).thenReturn(true);
        doThrow(new RuntimeException("COMPANY Not Found!")).when(companyRepository).delete(company);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            companyService.deleteCompany(companyName);
        });

        assertEquals("COMPANY Not Found!", exception.getMessage());

        verify(companyRepository, times(1)).findCompanyWithProductsByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(companyRepository, times(1)).delete(company);

    }

    /**
     * Test case to verify that the updateCompany method of the CompanyService
     * updates a Company successfully.
     *
     * @throws IOException if an error occurs during the file upload process
     */
    @Test
    void shouldUpdateCompanySuccessfully() throws IOException {
        companyRequestDTO = getCompanyRequestDTO();
        companyResponseDTO = getCompanyResponseDTO();

        when(companyRepository.findByName(companyName)).thenReturn(Optional.of(company));
        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class))).thenReturn(imageUrl);
        when(mapper.companyTOCompanyResponseDTO(any(Company.class))).thenReturn(companyResponseDTO);
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyResponseDTO result = companyService.updateCompany(companyName, companyRequestDTO);

        assertEquals(companyResponseDTO.getName(), result.getName());
        assertEquals(companyResponseDTO.getImageUrl(), result.getImageUrl());

        verify(companyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(companyRepository, times(1)).save(any(Company.class));
        verify(mapper, times(1)).companyTOCompanyResponseDTO(any(Company.class));
    }

    /**
     * Test case to verify that a NotFoundException is thrown when the Company
     * does not exist during the updateCompany method.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void shouldThrowNotFoundExceptionWhenUpdateCompanyDoesNotExist() throws IOException {
        when(companyRepository.findByName(companyName)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            companyService.updateCompany(companyName, getCompanyRequestDTO());
        });

        assertEquals(NotFoundTypeException.COMPANY + " Not Found!", exception.getMessage());

        verify(companyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(0)).deleteImageFromFileSystem(anyString());
        verify(companyRepository, times(0)).save(any(Company.class));
    }

    /**
     * Test case to verify that an IOException is thrown when the image deletion
     * fails during the updateCompany method.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void shouldThrowIOExceptionWhenImageDeletionFailsWhenUpdateCompany() throws IOException {
        companyRequestDTO = getCompanyRequestDTO();

        when(companyRepository.findByName(companyName)).thenReturn(Optional.of(company));
        doThrow(new IOException("Failed to delete image")).when(fileHelper)
                .deleteImageFromFileSystem(company.getImageUrl());

        IOException exception = assertThrows(IOException.class, () -> {
            companyService.updateCompany(companyName, companyRequestDTO);
        });

        assertEquals("Failed to delete image", exception.getMessage());

        verify(companyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(fileHelper, times(0)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(companyRepository, times(0)).save(any(Company.class));
    }

    /**
     * Test case to verify that an IOException is thrown when the image upload
     * fails during the updateCompany method.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void shouldThrowIOExceptionWhenImageUploadFailsWhenUpdateCompany() throws IOException {
        companyRequestDTO = getCompanyRequestDTO();

        when(companyRepository.findByName(companyName)).thenReturn(Optional.of(company));
        when(fileHelper.deleteImageFromFileSystem(company.getImageUrl())).thenReturn(true);
        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class)))
                .thenThrow(new IOException("File upload failed"));

        IOException exception = assertThrows(IOException.class, () -> {
            companyService.updateCompany(companyName, companyRequestDTO);
        });

        assertEquals("File upload failed", exception.getMessage());

        verify(companyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(companyRepository, times(0)).save(any(Company.class));
    }

    /**
     * This test method verifies that the Company service does not update the
     * Company when no changes are provided.
     *
     * @throws IOException if an I/O error occurs during the test
     */
    @Test
    void shouldNotUpdateCompanyWhenNoChangesProvided() throws IOException {

        CompanyRequestDTO emptyRequestDTO = new CompanyRequestDTO(null, null);

        when(companyRepository.findByName(companyName)).thenReturn(Optional.of(company));
        when(mapper.companyTOCompanyResponseDTO(any(Company.class))).thenReturn(getCompanyResponseDTO());
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyResponseDTO result = companyService.updateCompany(companyName, emptyRequestDTO);

        assertEquals(companyName, result.getName());
        assertEquals(imageUrl, result.getImageUrl());

        verify(companyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(0)).deleteImageFromFileSystem(anyString());
        verify(fileHelper, times(0)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(companyRepository, times(1)).save(any(Company.class));
        verify(mapper, times(1)).companyTOCompanyResponseDTO(any(Company.class));

    }

}
