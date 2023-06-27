package tfip.project.Repositories;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Repository
public class ImageRepository {
    
    @Autowired
    private AmazonS3 s3Client;

	@Value("${DO_STORAGE_BUCKETNAME}")
    private String bucketName;

	@Value("${DO_STORAGE_ENDPOINT}")
    private String endpoint;

    public Optional<String> uploadImage(MultipartFile file, String recipeId) throws IOException, AmazonS3Exception {
        try {
            String fileName = file.getOriginalFilename();
            Map<String, String> userData = new HashMap<>();
            userData.put("recipeId", recipeId);

            ObjectMetadata metadata = new ObjectMetadata(); 
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            metadata.setUserMetadata(userData);

            PutObjectRequest putRequest = new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata); // set title as key
            putRequest.withCannedAcl(CannedAccessControlList.PublicRead); 
            s3Client.putObject(putRequest); 
            return Optional.of("https://"+bucketName+"."+endpoint+"/"+fileName);

        } catch (AmazonS3Exception ex) {
            ex.printStackTrace();
            return Optional.empty(); 
        }
    }

}
