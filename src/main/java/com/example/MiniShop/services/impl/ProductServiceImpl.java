package com.example.MiniShop.services.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.MiniShop.models.entity.Product;
import com.example.MiniShop.models.entity.ProductImage;
import com.example.MiniShop.repository.ProductImageRepository;
import com.example.MiniShop.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl {
    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final S3ServiceImpl s3Service;

   
}
