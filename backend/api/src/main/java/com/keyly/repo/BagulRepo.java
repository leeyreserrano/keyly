package com.keyly.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Bagul;

@Repository
public interface BagulRepo extends JpaRepository<Bagul, Long>{
    
} 
