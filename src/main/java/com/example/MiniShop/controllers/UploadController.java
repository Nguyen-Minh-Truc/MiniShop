package com.example.MiniShop.controllers;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.response.UploadFileDTO;
import com.example.MiniShop.services.impl.S3ServiceImpl;
import com.example.MiniShop.util.annotation.ApiMessage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
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
  // gửi 1 ảnh
  @ApiMessage("upload File thành công. ")
  public ResponseEntity<?>
  uploadFile(@RequestParam(name = "file", required = false) MultipartFile file)
      throws IOException, NotFoundException, ConflictException {
    UploadFileDTO uploadFileDTO = this.s3Service.uploadMultiple(file);
    return ResponseEntity.ok().body(uploadFileDTO);
  }

  // gửi nhiều ảnh
  @PostMapping("/upload-multiple/product")
  public ResponseEntity<List<UploadFileDTO>>
  uploadMultiple(@RequestParam("files") List<MultipartFile> files)
      throws Exception {

    List<UploadFileDTO> result = s3Service.uploadMultiple(files);

    return ResponseEntity.ok(result);
  }

  // xoá ảnh
  @DeleteMapping("/delete/product/{id}")
  public ResponseEntity<String>
  deleteFile(@RequestParam(value = "url", required = false) String fileUrl,
             @PathVariable("id") long id) throws URISyntaxException {
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