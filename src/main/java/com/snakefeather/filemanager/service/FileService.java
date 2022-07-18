package com.snakefeather.filemanager.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public interface FileService {

     String save(MultipartFile file);
}
