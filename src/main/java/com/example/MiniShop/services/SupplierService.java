package com.example.MiniShop.services;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.models.entity.Supplier;
import com.example.MiniShop.models.request.SupplierReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.SupplierRepDto;

public interface SupplierService {
    SupplierRepDto createSupplier(SupplierReq supplierReq) throws ConflictException;
    ApiResponsePagination fetchAllSuppliers(Specification<Supplier> spec, Pageable pageable);
    SupplierRepDto fetchById(long id) throws NotFoundException;
    SupplierRepDto updateSupplierByID(long id, SupplierReq supplierReq) throws NotFoundException, ConflictException; 
}
