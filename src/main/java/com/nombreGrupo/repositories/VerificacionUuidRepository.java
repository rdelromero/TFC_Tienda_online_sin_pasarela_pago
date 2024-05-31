package com.nombreGrupo.repositories;

import com.nombreGrupo.modelo.entities.VerificacionUuid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificacionUuidRepository extends JpaRepository<VerificacionUuid, Integer> {
    Optional<VerificacionUuid> findByUuid(String UuidString);
    Optional<VerificacionUuid> findByUsuario_IdUsuario(int idUsuario);
}