package com.server.demo.Service;

import com.server.demo.Model.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) throws FileStorageException{
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException(this.fileStorageLocation.toString());
        }
    }

    public String storeFile(MultipartFile file, String newFileName){
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try{
            if (filename.contains("..")){
                throw new FileStorageException("Invalid file name");
            }

            String extension = filename.substring(filename.indexOf('.'));
            filename = newFileName+extension;

            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (FileStorageException fileStorageException) {
            fileStorageException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filename;
    }

    public Resource loadFileAsResource(String fileName) throws FileNotFoundException{
        try{
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()){
                return resource;
            }
            throw new FileNotFoundException();
        } catch (MalformedURLException e) {
            throw new FileNotFoundException();
        }
    }
}

class FileStorageException extends Exception{
    public FileStorageException(String msg){
        super(msg);
    }
}
