package com.luv2code.doan.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.luv2code.doan.exceptions.StorageUploadFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class StorageService {
    private final Logger log = LoggerFactory.getLogger(StorageService.class);

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    public String uploadFile(String base64String) throws StorageUploadFileException {
        String fileUrl = "";
        try {
            String fileNameDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
            File fileObj = getImageFromBase64(base64String, fileNameDate);
            String fileName = "image_" + System.currentTimeMillis() ;
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            fileUrl = String.valueOf(s3Client.getUrl(bucketName, fileName));

            fileObj.delete();
            log.info("File upload: "+ fileName + " ,File url is: " + fileUrl);
        }
        catch (Exception e) {
            throw new StorageUploadFileException("Error upload file to server");
        }
        return fileUrl;


    }

    public static File getImageFromBase64(String base64String, String fileName) throws StorageUploadFileException {
        String[] strings = base64String.split(",");
        String extension;
        switch (strings[0]) { // check image's extension
            case "data:image/jpeg;base64":
                extension = "jpeg";
                break;
            case "data:image/png;base64":
                extension = "png";
                break;
            default: // should write cases for more images types
                extension = "jpg";
                break;
        }


        // convert base64 string to binary data
        byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
        File file = new File(fileName + extension);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            outputStream.write(data);
        } catch (IOException e) {
            throw new StorageUploadFileException("Error upload file to server");
        }
        return file;
    }
}
