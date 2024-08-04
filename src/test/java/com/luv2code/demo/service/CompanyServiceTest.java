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
    private CompanyService CompanyService;

    @Mock
    private CompanyRepository CompanyRepository;

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

        when(CompanyRepository.findAllCompanies()).thenReturn(CompanyResponseDTOs);

        List<CompanyResponseDTO> result = CompanyService.getAllCompanies();

        assertEquals(CompanyResponseDTOs, result);

        verify(CompanyRepository, times(1)).findAllCompanies();

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

        when(CompanyRepository.findAllCompanies()).thenReturn(Companies);

        List<CompanyResponseDTO> result = CompanyService.getAllCompanies();

        assertTrue(result.isEmpty());

        verify(CompanyRepository, times(1)).findAllCompanies();

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
        when(CompanyRepository.save(any(Company.class))).thenReturn(company);

        CompanyResponseDTO result = CompanyService.createCompany(companyRequestDTO);

        assertEquals(companyResponseDTO.getName(), result.getName());
        assertEquals(companyResponseDTO.getImageUrl(), result.getImageUrl());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(mapper, times(1)).companyRequestDTOTOCompany(companyRequestDTO);
        verify(mapper, times(1)).companyTOCompanyResponseDTO(any(Company.class));
        verify(CompanyRepository, times(1)).save(any(Company.class));

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
            CompanyService.createCompany(invalidRequest);
        });

        verify(fileHelper, times(0)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(CompanyRepository, times(0)).save(any(Company.class));
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
        doThrow(new RuntimeException("Failed to save Company")).when(CompanyRepository).save(any(Company.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            CompanyService.createCompany(companyRequestDTO);
        });

        assertEquals("Failed to save Company", exception.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(CompanyRepository, times(1)).save(company);
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
            CompanyService.createCompany(companyRequestDTO);
        });

        assertEquals("File upload failed", exception.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(CompanyRepository, times(0)).save(any(Company.class));
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
            CompanyService.createCompany(CompanyRequestDTO);
        });

        assertEquals("Mapping to Company failed", thrown.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(mapper, times(1)).companyRequestDTOTOCompany(CompanyRequestDTO);
        verify(mapper, times(0)).companyTOCompanyResponseDTO(any(Company.class));
        verify(CompanyRepository, times(0)).save(any(Company.class));
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
        when(CompanyRepository.save(any(Company.class))).thenReturn(company);
        doThrow(new RuntimeException("Mapping to ResponseDTO failed")).when(mapper)
                .companyTOCompanyResponseDTO(company);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            CompanyService.createCompany(CompanyRequestDTO);
        });

        assertEquals("Mapping to ResponseDTO failed", thrown.getMessage());

        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(mapper, times(1)).companyRequestDTOTOCompany(CompanyRequestDTO);
        verify(mapper, times(1)).companyTOCompanyResponseDTO(company);
        verify(CompanyRepository, times(1)).save(any(Company.class));

    }

    /**
     * Test case to verify that a NotFoundException is thrown when the Company
     * is not found when deleting a Company.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldThrowNotFoundExceptionWhenCompanyNotFoundWhenDeletingCompany() throws IOException {

        when(CompanyRepository.findByName(companyName)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            CompanyService.deleteCompany(companyName);
        });

        assertEquals(NotFoundTypeException.COMPANY + " Not Found!", exception.getMessage());

        verify(CompanyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(0)).deleteImageFromFileSystem(anyString());
        verify(CompanyRepository, times(0)).delete(any(Company.class));

    }

    /**
     * Test case to verify that the deleteCompany method handles image deletion
     * failure when deleting a Company.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldHandleImageDeletionFailureWhenDeletingCompany() throws IOException {

        when(CompanyRepository.findByName(companyName)).thenReturn(Optional.of(company));
        doThrow(new IOException("Failed to delete image")).when(fileHelper).deleteImageFromFileSystem(company.getImageUrl());
        doNothing().when(CompanyRepository).delete(company);

        IOException exception = assertThrows(IOException.class, () -> {
            CompanyService.deleteCompany(companyName);
        });

        assertEquals("Failed to delete image", exception.getMessage());

        verify(CompanyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(CompanyRepository, times(0)).delete(company);

    }

    /**
     * This test method verifies that the deleteCompany method successfully
     * deletes a Company.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldDeleteCompanySuccessfully() throws IOException {

        when(CompanyRepository.findByName(companyName)).thenReturn(Optional.of(company));
        when(fileHelper.deleteImageFromFileSystem(company.getImageUrl())).thenReturn(true);
        doNothing().when(CompanyRepository).delete(any(Company.class));

        ResponseEntity<ApiResponseDTO> response = CompanyService.deleteCompany(companyName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success Delete Company.", response.getBody().getMessage());

        verify(CompanyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(CompanyRepository, times(1)).delete(company);

    }

    /**
     * Test case to verify that the deleteCompany method handles exceptions
     * during deletion of a Company.
     *
     * @throws IOException if an error occurs during the file deletion process
     */
    @Test
    void shouldHandleExceptionDuringDeleteWhenDeletingCompany() throws IOException {

        when(CompanyRepository.findByName(companyName)).thenReturn(Optional.of(company));
        when(fileHelper.deleteImageFromFileSystem(company.getImageUrl())).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(CompanyRepository).delete(company);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            CompanyService.deleteCompany(companyName);
        });

        assertEquals("Database error", exception.getMessage());

        verify(CompanyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(CompanyRepository, times(1)).delete(company);

    }

    /**
     * Test case to verify that the `existCompanyByName` method returns `true`
     * when the Company exists.
     *
     * This test case sets up a mocked `CompanyRepository` to return `true` when
     * `existsByName` is called with the specified `CompanyName`. Then it calls
     * the `existCompanyByName` method with the same `CompanyName` and asserts
     * that the returned value is `true`. Finally, it verifies that
     * `existsByName` was called exactly once with the specified `CompanyName`.
     *
     * @throws Exception if an error occurs during the test execution
     */
    @Test
    void shouldReturnTrueWhenCompanyExists() {

        when(CompanyRepository.existsByName(companyName)).thenReturn(true);

        Boolean result = CompanyService.existCompanyByName(companyName);

        assertTrue(result);

        verify(CompanyRepository, times(1)).existsByName(companyName);

    }

    /**
     * Test case to verify that a NotFoundException is thrown when the Company
     * does not exist.
     *
     * @throws NotFoundException if the Company is not found
     */
    @Test
    void shouldThrowNotFoundExceptionWhenCompanyDoesNotExist() {

        when(CompanyRepository.existsByName(companyName)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            CompanyService.existCompanyByName(companyName);
        });

        assertEquals(NotFoundTypeException.COMPANY + " Not Found!", exception.getMessage());

        verify(CompanyRepository, times(1)).existsByName(companyName);
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

        when(CompanyRepository.findByName(companyName)).thenReturn(Optional.of(company));
        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class))).thenReturn(imageUrl);
        when(mapper.companyTOCompanyResponseDTO(any(Company.class))).thenReturn(companyResponseDTO);
        when(CompanyRepository.save(any(Company.class))).thenReturn(company);

        CompanyResponseDTO result = CompanyService.updateCompany(companyName, companyRequestDTO);

        assertEquals(companyResponseDTO.getName(), result.getName());
        assertEquals(companyResponseDTO.getImageUrl(), result.getImageUrl());

        verify(CompanyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(CompanyRepository, times(1)).save(any(Company.class));
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
        when(CompanyRepository.findByName(companyName)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            CompanyService.updateCompany(companyName, getCompanyRequestDTO());
        });

        assertEquals(NotFoundTypeException.COMPANY + " Not Found!", exception.getMessage());

        verify(CompanyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(0)).deleteImageFromFileSystem(anyString());
        verify(CompanyRepository, times(0)).save(any(Company.class));
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

        when(CompanyRepository.findByName(companyName)).thenReturn(Optional.of(company));
        doThrow(new IOException("Failed to delete image")).when(fileHelper)
                .deleteImageFromFileSystem(company.getImageUrl());

        IOException exception = assertThrows(IOException.class, () -> {
            CompanyService.updateCompany(companyName, companyRequestDTO);
        });

        assertEquals("Failed to delete image", exception.getMessage());

        verify(CompanyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(fileHelper, times(0)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(CompanyRepository, times(0)).save(any(Company.class));
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

        when(CompanyRepository.findByName(companyName)).thenReturn(Optional.of(company));
        when(fileHelper.deleteImageFromFileSystem(company.getImageUrl())).thenReturn(true);
        when(fileHelper.uploadFileToFileSystem(any(MultipartFile.class)))
                .thenThrow(new IOException("File upload failed"));

        IOException exception = assertThrows(IOException.class, () -> {
            CompanyService.updateCompany(companyName, companyRequestDTO);
        });

        assertEquals("File upload failed", exception.getMessage());

        verify(CompanyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(1)).deleteImageFromFileSystem(company.getImageUrl());
        verify(fileHelper, times(1)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(CompanyRepository, times(0)).save(any(Company.class));
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

        when(CompanyRepository.findByName(companyName)).thenReturn(Optional.of(company));
        when(mapper.companyTOCompanyResponseDTO(any(Company.class))).thenReturn(getCompanyResponseDTO());
        when(CompanyRepository.save(any(Company.class))).thenReturn(company);

        CompanyResponseDTO result = CompanyService.updateCompany(companyName, emptyRequestDTO);

        assertEquals(companyName, result.getName());
        assertEquals(imageUrl, result.getImageUrl());

        verify(CompanyRepository, times(1)).findByName(companyName);
        verify(fileHelper, times(0)).deleteImageFromFileSystem(anyString());
        verify(fileHelper, times(0)).uploadFileToFileSystem(any(MultipartFile.class));
        verify(CompanyRepository, times(1)).save(any(Company.class));
        verify(mapper, times(1)).companyTOCompanyResponseDTO(any(Company.class));

    }

}
