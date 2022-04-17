package com.smart.garage.utility;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.smart.garage.models.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
public class CloudinaryHelper {

    public static final String CLOUDINARY_PUBLIC_ID = "SmartGarage/VisitPhotos/visitID-";
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryHelper(Environment env) {
        String cloudName = env.getProperty("cloud_name");
        String apiKey = env.getProperty("api_key");
        String apiSecret = env.getProperty("api_secret");

        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }

    public String uploadToCloudinary(MultipartFile photoAsFile, Visit visit, String uniqueToken) throws IOException {
        var params = ObjectUtils.asMap(
                "public_id", CLOUDINARY_PUBLIC_ID + visit.getId() + "/token-" + uniqueToken,
                "overwrite", true,
                "resource_type", "image");

        var uploadResult = cloudinary.uploader().upload(photoAsFile.getBytes(), params);

        return uploadResult.get("url").toString();
    }

    public void deleteFromCloudinary(Visit visit, String token) throws IOException {
        String assetName = CLOUDINARY_PUBLIC_ID + visit.getId() + "/token-" + token;

        cloudinary.uploader().destroy(assetName, Map.of());
    }

}
