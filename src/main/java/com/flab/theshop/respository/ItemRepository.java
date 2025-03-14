package com.flab.theshop.respository;

import com.flab.theshop.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.name like %:name% order by i.price desc")
    List<Item> findByName(@Param("name") String name);
}
