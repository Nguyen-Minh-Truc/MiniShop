package com.example.MiniShop.config;

import com.example.MiniShop.models.entity.Permission;
import com.example.MiniShop.models.entity.Role;
import com.example.MiniShop.models.entity.User;
import com.example.MiniShop.repository.PermissionRepository;
import com.example.MiniShop.repository.RoleRepository;
import com.example.MiniShop.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
  private static final String ROLE_CUSTOMER = "CUSTOMER";
  private static final String ROLE_SELLER = "SELLER";
  private static final String ROLE_ADMIN_SYSTEM = "ADMIN_SYSTEM";
  private static final String ROLE_SUPER_ADMIN = "ADMIN";

  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    initializePermissions();
    initializeRoles();
    initializeUsers();
  }

  private void initializePermissions() {
    Map<String, Permission> existingPermissions =
        permissionRepository.findAll().stream().collect(Collectors.toMap(
            permission
            -> buildPermissionKey(permission.getApiPath(),
                                  permission.getMethod()),
            permission -> permission, (first, second) -> first, HashMap::new));

    for (PermissionSeed permissionSeed : buildPermissionSeeds()) {
      String key =
          buildPermissionKey(permissionSeed.apiPath(), permissionSeed.method());
      Permission existingPermission = existingPermissions.get(key);

      if (existingPermission == null) {
        Permission createdPermission =
            createPermission(permissionSeed.name(), permissionSeed.apiPath(),
                             permissionSeed.method(), permissionSeed.module());
        permissionRepository.save(createdPermission);
        continue;
      }

      boolean hasChanges = false;
      if (!Objects.equals(existingPermission.getName(),
                          permissionSeed.name())) {
        existingPermission.setName(permissionSeed.name());
        hasChanges = true;
      }
      if (!Objects.equals(existingPermission.getModule(),
                          permissionSeed.module())) {
        existingPermission.setModule(permissionSeed.module());
        hasChanges = true;
      }

      if (hasChanges) {
        permissionRepository.save(existingPermission);
      }
    }
  }

  private void initializeRoles() {
    Map<String, Permission> permissionByName =
        permissionRepository.findAll().stream().collect(Collectors.toMap(
            Permission::getName,
            permission -> permission, (first, second) -> first, HashMap::new));

    createOrUpdateRole(
        ROLE_CUSTOMER,
        "Customer role with product browsing, cart and order permissions",
        customerPermissionNames(), permissionByName);
    createOrUpdateRole(
        ROLE_SELLER,
        "Seller role with product CRUD, inventory management and order viewing",
        sellerPermissionNames(), permissionByName);
    createOrUpdateRole(
        ROLE_ADMIN_SYSTEM,
        "System admin role with user, role and system statistics permissions",
        adminSystemPermissionNames(), permissionByName);
    createOrUpdateRole(ROLE_SUPER_ADMIN,
                       "Super admin role with full permissions",
                       permissionByName.keySet(), permissionByName);
  }

  private void initializeUsers() {
    createOrUpdateUser(ROLE_CUSTOMER, "customer@gmail.com", "Customer");
    createOrUpdateUser(ROLE_SELLER, "seller@gmail.com", "Seller");
    createOrUpdateUser(ROLE_ADMIN_SYSTEM, "admin_system@gmail.com",
                       "Admin System");
    createOrUpdateUser(ROLE_SUPER_ADMIN, "admin@gmail.com",
                       "Super Admin");
  }

  private void createOrUpdateRole(String roleName, String description,
                                  Set<String> permissionNames,
                                  Map<String, Permission> permissionByName) {
    Role role = roleRepository.findByName(roleName);
    if (role == null) {
      role = new Role();
      role.setName(roleName);
    }

    List<String> missingPermissionNames =
        permissionNames.stream()
            .filter(
                permissionName -> !permissionByName.containsKey(permissionName))
            .sorted()
            .collect(Collectors.toList());
    if (!missingPermissionNames.isEmpty()) {
      throw new IllegalStateException("Missing permissions for role " +
                                      roleName + ": " + missingPermissionNames);
    }

    List<Permission> rolePermissions =
        permissionNames.stream()
            .map(permissionByName::get)
            .collect(Collectors.toCollection(ArrayList::new));

    role.setDescription(description);
    role.setPermissions(rolePermissions);
    roleRepository.save(role);
  }

  private void createOrUpdateUser(String roleName, String email,
                                  String username) {
    Role role = roleRepository.findByName(roleName);
    if (role == null) {
      throw new IllegalStateException("Role does not exist: " + roleName);
    }

    User user = userRepository.findByEmail(email);
    if (user == null) {
      user = new User();
      user.setEmail(email);
      user.setPassword(passwordEncoder.encode("123123"));
    }

    user.setUsername(username);
    user.setAddress("Ha Noi");
    user.setPhone("0123456789");
    user.setActive(true);
    user.setRole(role);
    userRepository.save(user);
  }

  private List<PermissionSeed> buildPermissionSeeds() {
    return List.of(
        // Auth
        new PermissionSeed("LOGIN", "/api/v1/auth/login", "POST", "Auth"),
        new PermissionSeed("REGISTER", "/api/v1/auth/register", "POST", "Auth"),
        new PermissionSeed("GET_ACCOUNT", "/api/v1/auth/account", "GET",
                           "Auth"),
        new PermissionSeed("REFRESH_TOKEN", "/api/v1/auth/refresh", "GET",
                           "Auth"),
        new PermissionSeed("LOGOUT", "/api/v1/auth/logout", "POST", "Auth"),

        // Users
        new PermissionSeed("GET_ALL_USERS", "/api/v1/users", "GET", "Users"),
        new PermissionSeed("CREATE_USER", "/api/v1/users", "POST", "Users"),
        new PermissionSeed("GET_USER", "/api/v1/users/{id}", "GET", "Users"),
        new PermissionSeed("UPDATE_USER", "/api/v1/users/{id}", "PUT", "Users"),

        // Roles
        new PermissionSeed("GET_ALL_ROLES", "/api/v1/roles", "GET", "Roles"),
        new PermissionSeed("CREATE_ROLE", "/api/v1/roles", "POST", "Roles"),
        new PermissionSeed("GET_ROLE", "/api/v1/roles/{id}", "GET", "Roles"),
        new PermissionSeed("UPDATE_ROLE", "/api/v1/roles/{id}", "PUT", "Roles"),

        // Permissions
        new PermissionSeed("GET_ALL_PERMISSIONS", "/api/v1/permissions", "GET",
                           "Permissions"),
        new PermissionSeed("CREATE_PERMISSION", "/api/v1/permissions", "POST",
                           "Permissions"),
        new PermissionSeed("GET_PERMISSION", "/api/v1/permissions/{id}", "GET",
                           "Permissions"),
        new PermissionSeed("UPDATE_PERMISSION", "/api/v1/permissions/{id}",
                           "PUT", "Permissions"),
        new PermissionSeed("DELETE_PERMISSION", "/api/v1/permissions/{id}",
                           "DELETE", "Permissions"),

        // Categories
        new PermissionSeed("GET_ALL_CATEGORIES", "/api/v1/categories", "GET",
                           "Categories"),
        new PermissionSeed("CREATE_CATEGORY", "/api/v1/categories", "POST",
                           "Categories"),
        new PermissionSeed("GET_CATEGORY", "/api/v1/categories/{id}", "GET",
                           "Categories"),
        new PermissionSeed("UPDATE_CATEGORY", "/api/v1/categories/{id}", "PUT",
                           "Categories"),

        // Products
        new PermissionSeed("GET_ALL_PRODUCTS", "/api/v1/products", "GET",
                           "Products"),
        new PermissionSeed("CREATE_PRODUCT", "/api/v1/products", "POST",
                           "Products"),
        new PermissionSeed("GET_PRODUCT", "/api/v1/products/{id}", "GET",
                           "Products"),
        new PermissionSeed("UPDATE_PRODUCT", "/api/v1/products/{id}", "PUT",
                           "Products"),

        // Carts
        new PermissionSeed("GET_MY_CART", "/api/v1/carts/me", "GET", "Carts"),
        new PermissionSeed("ADD_CART_ITEM", "/api/v1/carts/items", "POST",
                           "Carts"),
        new PermissionSeed("UPDATE_CART_ITEM", "/api/v1/carts/items/{id}",
                           "PUT", "Carts"),
        new PermissionSeed("DELETE_CART_ITEM", "/api/v1/carts/items/{id}",
                           "DELETE", "Carts"),
        new PermissionSeed("CLEAR_MY_CART", "/api/v1/carts/me", "DELETE",
                           "Carts"),

        // Orders
        new PermissionSeed("CHECKOUT_ORDER", "/api/v1/orders/checkout", "POST",
                           "Orders"),
        new PermissionSeed("PAY_ORDER", "/api/v1/orders/{id}/pay", "PUT",
                           "Orders"),
        new PermissionSeed("GET_MY_ORDERS", "/api/v1/orders/me", "GET",
                           "Orders"),
        new PermissionSeed("GET_ALL_ORDERS", "/api/v1/orders", "GET", "Orders"),
        new PermissionSeed("GET_ORDER", "/api/v1/orders/{id}", "GET", "Orders"),
        new PermissionSeed("CANCEL_ORDER", "/api/v1/orders/{id}/cancel", "PUT",
                           "Orders"),
        new PermissionSeed("MARK_ORDER_SUCCESS", "/api/v1/orders/{id}/success",
                           "PUT", "Orders"),

        // Inventories
        new PermissionSeed("GET_ALL_INVENTORIES", "/api/v1/inventories", "GET",
                           "Inventories"),
        new PermissionSeed("CREATE_INVENTORY", "/api/v1/inventories", "POST",
                           "Inventories"),
        new PermissionSeed("GET_INVENTORY", "/api/v1/inventories/{id}", "GET",
                           "Inventories"),
        new PermissionSeed("UPDATE_INVENTORY", "/api/v1/inventories/{id}",
                           "PUT", "Inventories"),

        // Promotions
        new PermissionSeed("GET_ALL_PROMOTIONS", "/api/v1/promotions", "GET",
                           "Promotions"),
        new PermissionSeed("CREATE_PROMOTION", "/api/v1/promotions", "POST",
                           "Promotions"),
        new PermissionSeed("GET_PROMOTION", "/api/v1/promotions/{id}", "GET",
                           "Promotions"),
        new PermissionSeed("UPDATE_PROMOTION", "/api/v1/promotions/{id}", "PUT",
                           "Promotions"),

        // Purchase Orders
        new PermissionSeed("GET_ALL_PURCHASE_ORDERS", "/api/v1/purchase-orders",
                           "GET", "Purchase Orders"),
        new PermissionSeed("CREATE_PURCHASE_ORDER", "/api/v1/purchase-orders",
                           "POST", "Purchase Orders"),
        new PermissionSeed("GET_PURCHASE_ORDER", "/api/v1/purchase-orders/{id}",
                           "GET", "Purchase Orders"),
        new PermissionSeed("UPDATE_PURCHASE_ORDER",
                           "/api/v1/purchase-orders/{id}", "PUT",
                           "Purchase Orders"),
        new PermissionSeed("CONFIRM_PURCHASE_ORDER",
                           "/api/v1/purchase-orders/{id}/confirm", "PUT",
                           "Purchase Orders"),
        new PermissionSeed("CANCEL_PURCHASE_ORDER",
                           "/api/v1/purchase-orders/{id}/cancel", "PUT",
                           "Purchase Orders"),
        new PermissionSeed("COMPLETE_PURCHASE_ORDER",
                           "/api/v1/purchase-orders/{id}/success", "PUT",
                           "Purchase Orders"),

        // Suppliers
        new PermissionSeed("CREATE_SUPPLIER", "/api/v1/suppliers", "POST",
                           "Suppliers"),
        new PermissionSeed("GET_ALL_SUPPLIERS", "/api/v1/suppliers", "GET",
                           "Suppliers"),
        new PermissionSeed("GET_SUPPLIER", "/api/v1/suppliers/{id}", "GET",
                           "Suppliers"),
        new PermissionSeed("UPDATE_SUPPLIER", "/api/v1/suppliers/{id}", "PUT",
                           "Suppliers"),

        // Files
        new PermissionSeed("UPLOAD_PRODUCT_FILE", "/api/v1/file/upload/product",
                           "POST", "Files"),
        new PermissionSeed("UPLOAD_MULTIPLE_PRODUCT_FILES",
                           "/api/v1/file/upload-multiple/product", "POST",
                           "Files"),
        new PermissionSeed("DELETE_PRODUCT_FILE",
                           "/api/v1/file/delete/product/{id}", "DELETE",
                           "Files"),

        // Dashboard (assumed for future implementation)
        new PermissionSeed("VIEW_DASHBOARD_STATS", "/api/v1/dashboard/stats",
                           "GET", "Dashboard"),
        new PermissionSeed("VIEW_ORDER_STATUS_STATS",
                           "/api/v1/dashboard/orders/by-status", "GET",
                           "Dashboard"),
        new PermissionSeed("VIEW_REVENUE_STATS", "/api/v1/dashboard/revenue",
               "GET", "Dashboard"),
        new PermissionSeed("VIEW_TOP_PRODUCTS_STATS",
               "/api/v1/dashboard/products/top", "GET",
               "Dashboard"),
        new PermissionSeed("VIEW_LOW_STOCK_STATS",
               "/api/v1/dashboard/inventory/low-stock", "GET",
               "Dashboard"),
        new PermissionSeed("VIEW_PROFIT_STATS", "/api/v1/dashboard/profit",
               "GET", "Dashboard"),
        new PermissionSeed("VIEW_PURCHASE_COST_STATS",
               "/api/v1/dashboard/purchase-cost", "GET",
               "Dashboard"),
        new PermissionSeed("VIEW_NEW_USERS_STATS",
               "/api/v1/dashboard/users/new", "GET",
               "Dashboard"));
  }

  private Set<String> customerPermissionNames() {
    return Set.of("LOGIN", "REGISTER", "GET_ACCOUNT", "REFRESH_TOKEN", "LOGOUT",
                  "GET_ALL_PRODUCTS", "GET_PRODUCT", "GET_ALL_CATEGORIES",
                  "GET_CATEGORY", "GET_ALL_PROMOTIONS", "GET_PROMOTION",
                  "GET_MY_CART", "ADD_CART_ITEM", "UPDATE_CART_ITEM",
                  "DELETE_CART_ITEM", "CLEAR_MY_CART", "CHECKOUT_ORDER",
                  "PAY_ORDER", "GET_MY_ORDERS", "GET_ORDER", "CANCEL_ORDER");
  }

  private Set<String> sellerPermissionNames() {
    return Set.of("LOGIN", "GET_ACCOUNT", "REFRESH_TOKEN", "LOGOUT",
                  "GET_ALL_PRODUCTS", "CREATE_PRODUCT", "GET_PRODUCT",
                  "UPDATE_PRODUCT", "GET_ALL_CATEGORIES", "GET_CATEGORY",
                  "GET_ALL_INVENTORIES", "CREATE_INVENTORY", "GET_INVENTORY",
                  "UPDATE_INVENTORY", "GET_ALL_ORDERS", "GET_ORDER",
                  "GET_ALL_PROMOTIONS", "CREATE_PROMOTION", "GET_PROMOTION",
                  "UPDATE_PROMOTION", "UPLOAD_PRODUCT_FILE",
                  "UPLOAD_MULTIPLE_PRODUCT_FILES", "DELETE_PRODUCT_FILE");
  }

  private Set<String> adminSystemPermissionNames() {
    return new HashSet<>(
        Set.of("LOGIN", "GET_ACCOUNT", "REFRESH_TOKEN", "LOGOUT",
               "GET_ALL_USERS", "CREATE_USER", "GET_USER", "UPDATE_USER",
               "GET_ALL_ROLES", "CREATE_ROLE", "GET_ROLE", "UPDATE_ROLE",
               "GET_ALL_PERMISSIONS", "GET_PERMISSION", "VIEW_DASHBOARD_STATS",
               "VIEW_ORDER_STATUS_STATS", "VIEW_REVENUE_STATS",
               "VIEW_TOP_PRODUCTS_STATS", "VIEW_LOW_STOCK_STATS",
               "VIEW_PROFIT_STATS", "VIEW_PURCHASE_COST_STATS",
               "VIEW_NEW_USERS_STATS"));
  }

  private String buildPermissionKey(String apiPath, String method) {
    return method + "::" + apiPath;
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

  private record PermissionSeed(String name, String apiPath, String method,
                                String module) {}
}
