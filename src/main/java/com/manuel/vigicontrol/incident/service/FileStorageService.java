package com.manuel.vigicontrol.incident.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${app.upload.dir}")
    private String uploadDir;

    public String store(MultipartFile file) {
        try {
            Path dir = Paths.get(uploadDir);

            if (!Files.exists(dir)) Files.createDirectories(dir);

            String ext = "";

            String name = file.getOriginalFilename();

            if (name != null && name.contains(".")) ext = name.substring(name.lastIndexOf("."));

            String filename = UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), dir.resolve(filename));

            return "/uploads/incidents/" + filename;

        } catch (IOException e) {

            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
        }
    }
}
