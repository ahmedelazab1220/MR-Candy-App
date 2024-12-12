package com.luv2code.demo.helper;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IFileHelper {

    public String uploadFileToFileSystem(MultipartFile file) throws IllegalStateException, IOException;

    public ResponseEntity<?> downloadImageFromFileSystem(String imageUrl) throws IOException;

    Boolean deleteImageFromFileSystem(String imageUrl) throws IOException;

}
