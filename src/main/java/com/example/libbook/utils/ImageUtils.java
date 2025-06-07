package com.example.libbook.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageUtils {

    private final Cloudinary cloudinary;

    public ImageUtils(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadAvatar(byte[] imgByte, int type) throws IOException {
        Map<Integer,String> attributes = new HashMap<Integer,String>();
        attributes.put(1, "avatar");
        attributes.put(2, "book");
        String folderName = attributes.get(type);
//        if (oldPublicId != null && !oldPublicId.trim().isEmpty()) {
//            Map<String, Object> options = ObjectUtils.asMap("invalidate", true);
//            Map<?, ?> deleteResult = cloudinary.uploader().destroy(oldPublicId, options);
//            System.out.println("Delete result: " + deleteResult);
//        }
        String uniqueFileName = UUID.randomUUID().toString();
        Map<?, ?> uploadAvatar = cloudinary.uploader().upload(imgByte, ObjectUtils.asMap(
                "folder", folderName,
                "public_id", uniqueFileName,
                "overwrite", true,
                "resource_type", "image"
        ));
        return uploadAvatar.get("secure_url").toString();
    }


}