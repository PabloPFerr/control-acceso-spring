package com.control.acceso.infrastructure.repository;

import com.control.acceso.domain.model.Registro;
import com.control.acceso.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {
    List<Registro> findByUsuarioOrderByHoraEntradaDesc(Usuario usuario);
    List<Registro> findByHoraEntradaBetween(LocalDateTime inicio, LocalDateTime fin);
    Optional<Registro> findFirstByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(Usuario usuario);
    List<Registro> findByUsuarioAndHoraEntradaBetween(Usuario usuario, LocalDateTime inicio, LocalDateTime fin);
    List<Registro> findByHoraSalidaIsNullAndHoraEntradaBetween(LocalDateTime inicio, LocalDateTime fin);
    boolean existsByUsuarioAndHoraSalidaIsNull(Usuario usuario);
    List<Registro> findAll();
}
