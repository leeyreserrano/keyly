package com.keyly.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Sucursal;

@Repository
public interface SucursalRepo extends JpaRepository<Sucursal, Long>{

}
