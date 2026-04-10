package com.mcart.inventory.repository;

import com.mcart.inventory.entity.InventoryItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, String> {

    @Query("select i from InventoryItem i where i.productId = :productId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventoryItem> findForUpdate(@Param("productId") String productId);

    @Modifying
    @Query("""
            update InventoryItem i
               set i.availableQty = i.availableQty - :qty,
                   i.updatedAt = :now
             where i.productId = :productId
               and i.availableQty >= :qty
            """)
    int decrementIfEnough(@Param("productId") String productId, @Param("qty") int qty, @Param("now") Instant now);

    @Modifying
    @Query("""
            update InventoryItem i
               set i.availableQty = i.availableQty + :qty,
                   i.updatedAt = :now
             where i.productId = :productId
            """)
    int increment(@Param("productId") String productId, @Param("qty") int qty, @Param("now") Instant now);
}

