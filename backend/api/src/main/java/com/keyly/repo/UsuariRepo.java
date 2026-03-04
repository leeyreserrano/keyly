package com.keyly.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.keyly.model.Usuari;

@Repository
public interface UsuariRepo extends JpaRepository<Usuari, Long> {

    boolean existsByCorreu(String correu);

}
