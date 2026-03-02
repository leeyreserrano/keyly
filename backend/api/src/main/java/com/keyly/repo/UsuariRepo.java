package com.keyly.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keyly.model.Usuari;

public interface UsuariRepo extends JpaRepository<Usuari, Long> {

    boolean existsByCorreu(String correu);

}
