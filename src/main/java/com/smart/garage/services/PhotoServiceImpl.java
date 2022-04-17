package com.smart.garage.services;

import com.smart.garage.models.Photo;
import com.smart.garage.models.User;
import com.smart.garage.models.Visit;
import com.smart.garage.repositories.contracts.PhotoRepository;
import com.smart.garage.services.contracts.PhotoService;
import com.smart.garage.services.contracts.VisitService;
import com.smart.garage.utility.CloudinaryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;
    private final CloudinaryHelper cloudinaryHelper;
    private final VisitService visitService;

    @Autowired
    public PhotoServiceImpl(PhotoRepository photoRepository, CloudinaryHelper cloudinaryHelper, VisitService visitService) {
        this.photoRepository = photoRepository;
        this.cloudinaryHelper = cloudinaryHelper;
        this.visitService = visitService;
    }

    @Override
    public void save(User requester, MultipartFile photoAsFile, int visitID) throws IOException {
        Visit visit = visitService.getById(requester, visitID);
        String uniqueToken = UUID.randomUUID().toString();
        String photoURL = cloudinaryHelper.uploadToCloudinary(photoAsFile, visit, uniqueToken);
        Photo photo = new Photo(visit, photoURL, uniqueToken);
        photoRepository.create(photo);
    }

    @Override
    public void delete(User requester, int visitID, String token) throws IOException {
        Visit visit = visitService.getById(requester, visitID);
        Photo photo = photoRepository.getByField("token", token);
        cloudinaryHelper.deleteFromCloudinary(visit, photo.getToken());
        photoRepository.delete(photo.getId());
    }
}
