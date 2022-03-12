package com.luv2code.doan.controller;


import com.luv2code.doan.exceptions.StorageUploadFileException;
import com.luv2code.doan.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class StorageController {
    private final Logger log = LoggerFactory.getLogger(StorageController.class);
    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file) throws StorageUploadFileException {
        log.info(file.getName());
        return new ResponseEntity<>(storageService.uploadFile(file), HttpStatus.OK);
    }

}
