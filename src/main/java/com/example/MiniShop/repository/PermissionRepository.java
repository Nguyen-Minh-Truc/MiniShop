package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissionRepository
    extends JpaRepository<Permission, Long>,
            JpaSpecificationExecutor<Permission> {
  boolean existsByModuleAndApiPathAndMethod(String module, String apiPath,
                                         String method);
}
