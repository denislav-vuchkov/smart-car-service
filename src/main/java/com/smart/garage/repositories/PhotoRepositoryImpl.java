package com.smart.garage.repositories;

import com.smart.garage.models.Photo;
import com.smart.garage.repositories.contracts.PhotoRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PhotoRepositoryImpl extends AbstractCRUDRepository<Photo> implements PhotoRepository {

    public PhotoRepositoryImpl() {
        super(Photo.class);
    }

}
