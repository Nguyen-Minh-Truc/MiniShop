package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
    extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  boolean existsByEmail(String email);
  User findByEmail(String email);
  User findByRefreshTokenAndEmail(String token, String email);

  @Query("SELECT YEAR(u.createdAt), MONTH(u.createdAt), COUNT(u.id) " +
         "FROM User u " +
         "WHERE u.createdAt BETWEEN :from AND :to " +
         "GROUP BY YEAR(u.createdAt), MONTH(u.createdAt) " +
         "ORDER BY YEAR(u.createdAt), MONTH(u.createdAt)")
  List<Object[]> countNewUsersByMonth(@Param("from") LocalDateTime from,
                                      @Param("to") LocalDateTime to);
}
