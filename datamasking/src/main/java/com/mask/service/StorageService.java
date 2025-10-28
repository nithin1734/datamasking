package com.mask.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {

    @Value("${app.storage-dir:./storage}")
    private String storageDir;

    public Path store(String filename, InputStream inputStream) throws Exception {
        File dir = new File(storageDir);
        if (!dir.exists()) dir.mkdirs();

        String safeName = System.currentTimeMillis() + "_" + FilenameUtils.getName(filename);
        Path destination = Path.of(dir.getAbsolutePath(), safeName);
        Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        return destination;
    }

    public Path getPath(String storedName) {
        return Path.of(storageDir, storedName);
    }
}
