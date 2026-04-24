package com.example.MiniShop.mapper;

import com.example.MiniShop.models.entity.Supplier;
import com.example.MiniShop.models.request.SupplierReq;
import com.example.MiniShop.models.response.SupplierRepDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {
  public Supplier toEntity(SupplierReq sReq) {

    if (sReq == null)
      return null;
    Supplier supplier = new Supplier();
    supplier.setName(sReq.getName());
    supplier.setPhone(sReq.getName());
    supplier.setEmail(sReq.getEmail());
    supplier.setAddress(sReq.getAddress());
    supplier.setActive(true);
    return supplier;
  }

  public SupplierRepDto toDto(Supplier supplier) {
    if (supplier == null)
      return null;

    SupplierRepDto dto = new SupplierRepDto();
    dto.setId(supplier.getId());
    dto.setName(supplier.getName());
    dto.setPhone(supplier.getPhone());
    dto.setEmail(supplier.getEmail());
    dto.setAddress(supplier.getAddress());
    dto.setActive(supplier.isActive());
    return dto;
  };

  public List<SupplierRepDto> toDtoList(List<Supplier> suppliers) {
    List<SupplierRepDto> dtos = new ArrayList<>();

    if (suppliers != null) {
      for (Supplier supplier : suppliers) {
        if (supplier != null) {
          SupplierRepDto dto = new SupplierRepDto();
          dto.setId(supplier.getId());
          dto.setName(supplier.getName());
          dto.setPhone(supplier.getPhone());
          dto.setEmail(supplier.getEmail());
          dto.setAddress(supplier.getAddress());
          dtos.add(dto);
        }
      }
    }

    return dtos;
  }
}
