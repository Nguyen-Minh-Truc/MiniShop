package com.example.MiniShop.repository;

import com.example.MiniShop.models.entity.CartDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
  Optional<CartDetail> findByIdAndCartId(Long id, Long cartId);

  Optional<CartDetail> findByCartIdAndProductId(Long cartId, Long productId);

  List<CartDetail> findAllByCartId(Long cartId);
}