package com.example.MiniShop.config;

import com.example.MiniShop.models.entity.Permission;
import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.repository.PermissionRepository;
import com.example.MiniShop.repository.RoleRepository;
import com.example.MiniShop.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    // Initialize data only if database is empty
    if (permissionRepository.count() == 0 && roleRepository.count() == 0 &&
        userRepository.count() == 0) {
      initializePermissions();
      initializeRoles();
      initializeUsers();
    }
  }

  private void initializePermissions() {
    // Auth (5)
    permissionRepository.save(
        createPermission("LOGIN", "/api/v1/auth/login", "POST", "Auth"));
    permissionRepository.save(
        createPermission("REGISTER", "/api/v1/auth/register", "POST", "Auth"));
    permissionRepository.save(
        createPermission("GET_ACCOUNT", "/api/v1/auth/account", "GET", "Auth"));
    permissionRepository.save(
        createPermission("REFRESH_TOKEN", "/api/v1/auth/refresh", "POST", "Auth"));
    permissionRepository.save(
        createPermission("LOGOUT", "/api/v1/auth/logout", "POST", "Auth"));

    // Role (4)
    permissionRepository.save(
        createPermission("GET_ALL_ROLES", "/api/v1/roles", "GET", "Roles"));
    permissionRepository.save(
        createPermission("CREATE_ROLE", "/api/v1/roles", "POST", "Roles"));
    permissionRepository.save(
        createPermission("GET_ROLE_ID", "/api/v1/roles/{id}", "GET", "Roles"));
    permissionRepository.save(
        createPermission("UPDATE_ROLE", "/api/v1/roles/{id}", "PUT", "Roles"));

    // Permission (5)
    permissionRepository.save(createPermission(
        "GET_ALL_PERMISSIONS", "/api/v1/permissions", "GET", "Permissions"));
    permissionRepository.save(createPermission(
        "CREATE_PERMISSION", "/api/v1/permissions", "POST", "Permissions"));
    permissionRepository.save(createPermission(
        "GET_PERMISSION_ID", "/api/v1/permissions/{id}", "GET", "Permissions"));
    permissionRepository.save(createPermission(
        "UPDATE_PERMISSION", "/api/v1/permissions/{id}", "PUT", "Permissions"));
    permissionRepository.save(createPermission("DELETE_PERMISSION",
                                               "/api/v1/permissions/{id}",
                                               "DELETE", "Permissions"));

    // User (4)
    permissionRepository.save(
        createPermission("GET_ALL_USERS", "/api/v1/users", "GET", "Users"));
    permissionRepository.save(
        createPermission("CREATE_USER", "/api/v1/users", "POST", "Users"));
    permissionRepository.save(
        createPermission("GET_USER_ID", "/api/v1/users/{id}", "GET", "Users"));
    permissionRepository.save(
        createPermission("UPDATE_USER", "/api/v1/users/{id}", "PUT", "Users"));

    // Category (4)
    permissionRepository.save(createPermission(
        "GET_ALL_CATEGORIES", "/api/v1/categories", "GET", "Categories"));
    permissionRepository.save(createPermission(
        "CREATE_CATEGORY", "/api/v1/categories", "POST", "Categories"));
    permissionRepository.save(createPermission(
        "GET_CATEGORY_ID", "/api/v1/categories/{id}", "GET", "Categories"));
    permissionRepository.save(createPermission(
        "UPDATE_CATEGORY", "/api/v1/categories/{id}", "PUT", "Categories"));

    // Product (4)
    permissionRepository.save(createPermission(
        "GET_ALL_PRODUCTS", "/api/v1/products", "GET", "Products"));
    permissionRepository.save(createPermission(
        "CREATE_PRODUCT", "/api/v1/products", "POST", "Products"));
    permissionRepository.save(createPermission(
        "GET_PRODUCT_ID", "/api/v1/products/{id}", "GET", "Products"));
    permissionRepository.save(createPermission(
        "UPDATE_PRODUCT", "/api/v1/products/{id}", "PUT", "Products"));
  }

  private void initializeRoles() {
    List<Permission> allPermissions = permissionRepository.findAll();
    Role adminRole = new Role();
    adminRole.setName("ADMIN");
    adminRole.setUpdatedBy("SUPPER_ADMIN");
    adminRole.setDescription("Admin role with full permissions");
    adminRole.setPermissions(new ArrayList<>(allPermissions));
    roleRepository.save(adminRole);
  }

  private void initializeUsers() {
    Role adminRole = roleRepository.findByName("ADMIN");
    User adminUser = new User();
    adminUser.setUsername("Admin");
    adminUser.setEmail("admin@gmail.com");
    adminUser.setPassword(passwordEncoder.encode("123123"));
    adminUser.setAddress("Ha Noi");
    adminUser.setPhone("0123456789");
    adminUser.setActive(true);
    adminUser.setRole(adminRole);
    userRepository.save(adminUser);
  }

  private Permission createPermission(String name, String apiPath,
                                      String method, String module) {
    Permission permission = new Permission();
    permission.setName(name);
    permission.setApiPath(apiPath);
    permission.setMethod(method);
    permission.setModule(module);
    return permission;
  }
}
