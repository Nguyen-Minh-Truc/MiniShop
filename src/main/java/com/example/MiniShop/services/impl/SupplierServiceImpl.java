package com.example.MiniShop.services.impl;

import com.example.MiniShop.exception.custom.ConflictException;
import com.example.MiniShop.exception.custom.NotFoundException;
import com.example.MiniShop.mapper.SupplierMapper;
import com.example.MiniShop.models.entity.Supplier;
import com.example.MiniShop.models.request.SupplierReq;
import com.example.MiniShop.models.response.ApiResponsePagination;
import com.example.MiniShop.models.response.ApiResponsePagination.Meta;
import com.example.MiniShop.models.response.SupplierRepDto;
import com.example.MiniShop.repository.SupplierRepository;
import com.example.MiniShop.services.SupplierService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
  private final SupplierRepository supplierRepository;
  private final SupplierMapper supplierMapper;

  @Override
  public SupplierRepDto createSupplier(SupplierReq supplierReq)
      throws ConflictException {
    if (this.supplierRepository.existsByEmail(supplierReq.getEmail())) {
      throw new ConflictException("Email nhà cung cấp đã tồn tại.");
    }
    Supplier supplier =
        this.supplierRepository.save(supplierMapper.toEntity(supplierReq));

    SupplierRepDto dto = this.supplierMapper.toDto(supplier);

    return dto;
  }

  public ApiResponsePagination fetchAllSuppliers(Specification<Supplier> spec,
                                                 Pageable pageable) {
    Page<Supplier> suppliers = this.supplierRepository.findAll(spec, pageable);
    Meta meta = new Meta();

    meta.setPageCurrent(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());

    meta.setPages(suppliers.getTotalPages());
    meta.setTotal(suppliers.getTotalElements());

    ApiResponsePagination result = new ApiResponsePagination();

    result.setMeta(meta);

    result.setResult(this.supplierMapper.toDtoList(suppliers.getContent()));

    return result;
  }

  @Override
  public SupplierRepDto fetchById(long id) throws NotFoundException {
    Supplier supplier = this.supplierRepository.findById(id).orElseThrow(
        (() -> new NotFoundException("Supplier not found with id: " + id)));
    return supplierMapper.toDto(supplier);
  }

  @Override
  public SupplierRepDto updateSupplierByID(long id, SupplierReq supplierReq)
      throws NotFoundException, ConflictException {
    Supplier supplier = this.supplierRepository.findById(id).orElseThrow(
        (() -> new NotFoundException("Supplier not found with id: " + id)));

    Optional<Supplier> existingSupplier =
        this.supplierRepository.findByEmail(supplierReq.getEmail());

    if (existingSupplier.isPresent() && existingSupplier.get().getId() != id) {
      throw new ConflictException("Email nhà cung cấp đã tồn tại.");
    }

    supplier.setName(supplierReq.getName());
    supplier.setPhone(supplierReq.getName());
    supplier.setEmail(supplierReq.getEmail());
    supplier.setAddress(supplierReq.getAddress());
    supplier.setActive(true);

    Supplier updatedSupplier = this.supplierRepository.save(supplier);
    SupplierRepDto dto = this.supplierMapper.toDto(updatedSupplier);

    return dto;
  }
}
