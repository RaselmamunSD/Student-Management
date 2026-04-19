package com.example.Student.Management.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads", "submissions").toAbsolutePath().normalize();

    private void ensureRoot() throws IOException {
        Files.createDirectories(root);
    }

    public Path resolveStoredFile(String storedName) {
        return root.resolve(storedName).normalize();
    }

    public String storeSubmissionFile(MultipartFile file) throws IOException {
        ensureRoot();
        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String safe = original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String stored = UUID.randomUUID() + "_" + safe;
        Path target = root.resolve(stored);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return stored;
    }
}
