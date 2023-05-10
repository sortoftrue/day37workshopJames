package com.james.server.Controller;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.james.server.Model.Post;
import com.james.server.Repository.UploadRepo;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@CrossOrigin
@RestController
public class UploadController {

    @Autowired
    private AmazonS3 s3;

    private static final String BASE64_PREFIX = "data:;base64,";

    @Autowired
    private UploadRepo uploadRepo;

    @PostMapping(path = "/api/post")
    public ResponseEntity<String> postBlob(
            @RequestPart MultipartFile file,
            @RequestPart String comments) {

        System.out.println("Upload received");
        this.uploadRepo.upload(file, comments);

        return null;
    }

    @GetMapping(path = "/api/get/{id}")
    public ResponseEntity<String> getBlob(@PathVariable Integer id) {

        System.out.println("Getting blob");

        Post result = uploadRepo.getBlob(id);

        System.out.println(result.toString());

        String encodedString = Base64.getEncoder().encodeToString(result.getImage());
        JsonObject payload = Json.createObjectBuilder()
                .add("image", BASE64_PREFIX + encodedString)
                .add("comments", result.getComment())
                .build();

        return ResponseEntity.ok(payload.toString());

    }

    @PostMapping(path = "/api/postDigitalOcean")
    public ResponseEntity<String> postDigitalOcean(@RequestPart MultipartFile file,
            @RequestPart String comments) {

        Map<String, String> userData = new HashMap<>();
        userData.put("comments", comments);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        metadata.setUserMetadata(userData);

        PutObjectRequest putRequest;
        try {
            putRequest = new PutObjectRequest(
                    "james", "myobject%s".formatted(file.getOriginalFilename()), file.getInputStream(), metadata);

            putRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            s3.putObject(putRequest);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping(path = "/api/getDigitalOcean")
    public ResponseEntity<String> getDigitalOcean(@RequestParam String key) {

        byte[] buffer;
        String comments;
        S3Object result;

        try {
            GetObjectRequest getReq = new GetObjectRequest("james", key);
            result = s3.getObject(getReq);
            ObjectMetadata metadata = result.getObjectMetadata();
            Map<String, String> userData = metadata.getUserMetadata();
            try (S3ObjectInputStream is = result.getObjectContent()) {
                buffer = is.readAllBytes();
                comments = userData.get("comments");
                String encodedString = Base64.getEncoder().encodeToString(buffer);

                System.out.println(comments);
                System.out.println(encodedString);
                JsonObject payload = Json.createObjectBuilder()
                        .add("image", BASE64_PREFIX + encodedString)
                        .add("comments", comments)
                        .build();

                // System.out.println(payload.toString());
                return ResponseEntity.status(HttpStatus.OK)
                        .body(payload.toString());
            }
        } catch (AmazonS3Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("fail");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }

    }

}
