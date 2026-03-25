package com.example.MiniShop.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.response.UploadFileDTO;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl {

  private final AmazonS3 s3Client;

  @Value("${aws.bucketName}") private String bucketName;

  public UploadFileDTO uploadMultiple(MultipartFile file)
      throws NotFoundException, IOException, ConflictException {
    if (file == null || file.isEmpty()) {
      throw new NotFoundException("file is empty.");
    }
    String fileName = file.getOriginalFilename();
    List<String> allowedExtensions =
        Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");

    boolean isValid = allowedExtensions.stream().anyMatch(
        ext -> fileName.toLowerCase().endsWith(ext));

    if (!isValid) {
      throw new ConflictException("File không hợp lệ. Chỉ những file: " +
                                  allowedExtensions);
    }
    // đổi tên folder
    String folder = "product";
    String key = folder + "/" + fileName;
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());

    PutObjectRequest putObjectRequest =
        new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
            .withCannedAcl(CannedAccessControlList.PublicRead);

    s3Client.putObject(putObjectRequest);
    String fileUrl = s3Client.getUrl(bucketName, key).toString();
    UploadFileDTO uploadFileDTO = new UploadFileDTO(fileUrl, Instant.now());
    return uploadFileDTO;
  }

  public List<UploadFileDTO> uploadMultiple(List<MultipartFile> files)
      throws NotFoundException, ConflictException {

    if (files == null || files.isEmpty()) {
      throw new NotFoundException("Files are empty.");
    }

    List<String> allowedExtensions =
        Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
    String folder = "product";

    return files.parallelStream()
        .map(file -> {
          try {
            if (file == null || file.isEmpty())
              return null;

            String fileName = file.getOriginalFilename();

            boolean isValid = allowedExtensions.stream().anyMatch(
                ext -> fileName.toLowerCase().endsWith(ext));

            if (!isValid) {
              throw new RuntimeException("File không hợp lệ: " + fileName);
            }

            String key =
                folder + "/" + System.currentTimeMillis() + "_" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest request =
                new PutObjectRequest(bucketName, key, file.getInputStream(),
                                     metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(request);

            String url = s3Client.getUrl(bucketName, key).toString();

            return new UploadFileDTO(url, Instant.now());

          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        })
        .filter(dto -> dto != null)
        .toList();
  }

  

  // Xoá file theo URL upload
  public boolean deleteFileByUrl(String fileUrl) throws IOException {
    // 1. Lấy key từ URL
    String key = extractKeyFromUrl(fileUrl);

    // 2. Kiểm tra object tồn tại
    if (!s3Client.doesObjectExist(bucketName, key)) {
      return false;
    }

    // 3. Xoá object
    s3Client.deleteObject(bucketName, key);

    return true;
  }

  private String extractKeyFromUrl(String fileUrl) {
    // URL: https://my-bucket.s3.region.amazonaws.com/folder/file.png
    // => key = folder/file.png
    return fileUrl.substring(fileUrl.indexOf(".com/") + 5);
  }
}
