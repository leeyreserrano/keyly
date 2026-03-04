package com.keyly.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Domini;

@Repository
public interface DominiRepo extends JpaRepository<Domini, Long> {

}
