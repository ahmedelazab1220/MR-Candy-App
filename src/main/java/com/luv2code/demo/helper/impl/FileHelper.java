package com.luv2code.demo.helper.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.luv2code.demo.helper.IFileHelper;
import com.luv2code.demo.utils.FileUtils;

@Service
public class FileHelper implements IFileHelper {

	private final String FOLDER_PATH = "C:/Users/Lenovo/Documents/Github/Spring/StoreApp/assets/images/";

	@Override
	public String uploadFileToFileSystem(MultipartFile file) throws IllegalStateException, IOException {

		String imageUrl = FOLDER_PATH + file.getOriginalFilename() + " - " + UUID.randomUUID().toString();
		
		byte[] compressedFileData = FileUtils.compressFile(file.getBytes());

		Files.write(new File(imageUrl).toPath(), compressedFileData);

		return imageUrl;

	}

	@Override
	public ResponseEntity<byte[]> downloadImageFromFileSystem(String imageUrl) throws IOException {

		byte[] compressedFileData = Files.readAllBytes(new File(imageUrl).toPath());

		byte[] decompressedFileData = FileUtils.decompressFile(compressedFileData);

		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png"))
				.body(decompressedFileData);

	}

}
