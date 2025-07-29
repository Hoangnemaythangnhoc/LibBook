package com.example.libbook.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ImageUtils {

    private final Cloudinary cloudinary;

    public ImageUtils(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public byte[] decodeBase64(String base64){
        if(base64 == null || base64.isEmpty()) {
            throw new IllegalArgumentException("Base64 string cannot be null or empty");
        }
        if(base64.contains(",")) {
            base64 = base64.split(",")[1];
        }
        return Base64.getDecoder().decode(base64);
    }

    public String uploadAvatar(byte[] imgByte, int type) throws IOException {
        if (imgByte == null || imgByte.length == 0) {
            throw new IllegalArgumentException("Image data cannot be null or empty");
        }
        Map<Integer, String> attributes = new HashMap<>();
        attributes.put(1, "avatar");
        attributes.put(2, "book");
        String folderName = attributes.get(type);
        if (folderName == null) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
            String uniqueFileName = UUID.randomUUID().toString();
        Map<?, ?> uploadResult = cloudinary.uploader().upload(imgByte, ObjectUtils.asMap(
                "folder", folderName,
                "public_id", uniqueFileName,
                "overwrite", true,
                "resource_type", "image"
        ));
        Object secureUrl = uploadResult.get("secure_url");
        if (secureUrl == null) {
            throw new IOException("Failed to retrieve secure URL from Cloudinary");
        }
        return secureUrl.toString();
    }


}
