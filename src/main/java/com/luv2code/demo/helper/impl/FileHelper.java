package com.luv2code.demo.helper.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.luv2code.demo.helper.IFileHelper;
import com.luv2code.demo.utils.FileUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileHelper implements IFileHelper {

    @Value("${file.path}")
    private String FOLDER_PATH;

    @Override
    public String uploadFileToFileSystem(MultipartFile file) throws IllegalStateException, IOException {

        if (file.isEmpty()) {
            log.warn("Attempted to upload an empty file");
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        String contentType = file.getContentType();
        if (!"image/png".equals(contentType) && !"image/jpeg".equals(contentType) && !"image/svg+xml".equals(contentType)) {
            log.warn("Unsupported file type: {}", contentType);
            throw new IllegalArgumentException("Only PNG, JPEG, and SVG images are supported");
        }

        File directory = new File(FOLDER_PATH);
        if (!directory.exists()) {
            log.info("Directory {} does not exist, creating it", FOLDER_PATH);
            directory.mkdirs();
        }

        String sanitizedFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
        String imageUrl = FOLDER_PATH + UUID.randomUUID().toString() + " - " + sanitizedFilename;

        try {
            byte[] compressedFileData = FileUtils.compressFile(file.getBytes());
            Files.write(new File(imageUrl).toPath(), compressedFileData);
            log.info("File uploaded successfully to {}", imageUrl);
        } catch (IOException e) {
            log.error("Failed to save file to {}", imageUrl, e);
            throw new IOException("Failed to save file", e);
        }

        return imageUrl;
    }

    @Override
    public ResponseEntity<?> downloadImageFromFileSystem(String imageUrl) throws IOException {

        try {
            log.info("Attempting to download file from {}", imageUrl);
            byte[] compressedFileData = Files.readAllBytes(new File(imageUrl).toPath());
            byte[] decompressedFileData = FileUtils.decompressFile(compressedFileData);
            log.info("File downloaded successfully from {}", imageUrl);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(decompressedFileData);
        } catch (FileNotFoundException e) {
            log.warn("File not found at {}", imageUrl);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("File not found");
        } catch (IOException e) {
            log.error("Error reading file from {}", imageUrl, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file");
        }
    }

    @Override
    public Boolean deleteImageFromFileSystem(String imageUrl) throws IOException {
        log.info("Attempting to delete file from {}", imageUrl);
        Boolean fileIsDeleted = Files.deleteIfExists(Paths.get(imageUrl));
        if (fileIsDeleted) {
            log.info("File deleted successfully from {}", imageUrl);
        } else {
            log.warn("File not found at {} or could not be deleted", imageUrl);
        }
        return fileIsDeleted;
    }
}
