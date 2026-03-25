package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.response.UploadFileDTO;
import com.example.MiniShop.services.impl.S3ServiceImpl;
import com.example.MiniShop.util.annotation.ApiMessage;
import java.io.IOException;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class UploadController {

  private final S3ServiceImpl s3Service;
  @PostMapping("/upload/product")
  @ApiMessage("upload File thành công. ")
  public ResponseEntity<?>
  uploadFile(@RequestParam(name = "file", required = false) MultipartFile file)
      throws IOException, NotFoundException, ConflictException {
    UploadFileDTO uploadFileDTO = this.s3Service.uploadMultiple(file);
    return ResponseEntity.ok().body(uploadFileDTO);
  }

  @DeleteMapping("/delete/product")
  public ResponseEntity<String>
  deleteFile(@RequestParam(value = "url", required = false) String fileUrl)
      throws URISyntaxException {
    try {
      boolean deleted = this.s3Service.deleteFileByUrl(fileUrl);
      if (deleted) {
        return ResponseEntity.ok("Xoá file thành công");
      } else {
        return ResponseEntity.status(404).body("File không tồn tại");
      }
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body("Lỗi: " + e.getMessage());
    }
  }
}