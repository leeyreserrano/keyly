package com.keyly.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Carpeta;

@Repository
public interface CarpetaRepo extends JpaRepository<Carpeta, Long> {

}
