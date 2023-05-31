package com.commerce.backend.dao;

import com.commerce.backend.model.entity.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {

    void deleteByCartItemList_ProductVariant_Id(Long id);
    void deleteByUser_Id(Long id);
}
