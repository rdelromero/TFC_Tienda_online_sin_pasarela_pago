package com.nombreGrupo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nombreGrupo.modelo.entities.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Integer>{

}
