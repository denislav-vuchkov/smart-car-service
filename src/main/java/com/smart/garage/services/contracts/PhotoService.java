package com.smart.garage.services.contracts;

import com.smart.garage.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {
    void save(User requester, MultipartFile photoAsFile, int visitID) throws IOException;

    void delete(User requester, int visitID, String token) throws IOException;
}
