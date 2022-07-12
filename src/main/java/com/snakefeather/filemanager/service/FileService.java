package com.snakefeather.filemanager.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileService {

     String save(MultipartFile file);
}
