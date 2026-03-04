package com.keyly.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Item;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long>{

    
} 
