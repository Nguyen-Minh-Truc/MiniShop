package com.example.MiniShop.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.example.MiniShop.exception.custom.NotFoundException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class S3ServiceImplTest {

  @Mock private AmazonS3 s3Client;

  @InjectMocks private S3ServiceImpl s3Service;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(s3Service, "bucketName", "test-bucket");
  }

  @Nested
  class UploadSingleFileTests {
    @Test
    void uploadMultiple_WhenFileIsEmpty_ThrowsNotFoundException() {
      // Edge case: file null/empty phải báo lỗi.
      MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
      when(file.isEmpty()).thenReturn(true);

      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> s3Service.uploadMultiple(file));

      assertThat(thrown.getMessage()).isEqualTo("file is empty.");
    }
  }

  @Nested
  class UploadMultipleFilesTests {
    @Test
    void uploadMultiple_WhenFileListIsEmpty_ThrowsNotFoundException() {
      // Edge case: danh sách files rỗng.
      NotFoundException thrown = assertThrows(
          NotFoundException.class, () -> s3Service.uploadMultiple(List.of()));

      assertThat(thrown.getMessage()).isEqualTo("Files are empty.");
    }
  }

  @Nested
  class DeleteFileByUrlTests {
    @Test
    void deleteFileByUrl_WhenObjectDoesNotExist_ReturnsFalse()
        throws IOException {
      // Happy edge: object không tồn tại trên S3 thì trả về false.
      String fileUrl =
          "https://test-bucket.s3.ap-southeast-1.amazonaws.com/product/a.png";
      when(s3Client.doesObjectExist("test-bucket", "product/a.png"))
          .thenReturn(false);

      boolean result = s3Service.deleteFileByUrl(fileUrl);

      assertThat(result).isFalse();
    }
  }
}
