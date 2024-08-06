package com.luv2code.demo.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.luv2code.demo.helper.IFileHelper;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("${api.version}/files")
@AllArgsConstructor
public class FileController {

    private final IFileHelper fileHelper;

    @GetMapping("")
    public ResponseEntity<?> downloadImage(@RequestParam String imageUrl) throws IOException {

        return fileHelper.downloadImageFromFileSystem(imageUrl);

    }

}
