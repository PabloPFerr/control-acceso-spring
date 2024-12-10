package com.control.acceso.domain.port.outgoing;

import com.control.acceso.domain.model.Registro;
import com.control.acceso.domain.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistroRepositoryPort {
    Registro save(Registro registro);
    List<Registro> findByUsuarioOrderByHoraEntradaDesc(Usuario usuario);
    List<Registro> findByHoraEntradaBetween(LocalDateTime inicio, LocalDateTime fin);
    Optional<Registro> findFirstByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(Usuario usuario);
    List<Registro> findByHoraSalidaIsNullAndHoraEntradaBetween(LocalDateTime inicio, LocalDateTime fin);
    boolean existsByUsuarioAndHoraSalidaIsNull(Usuario usuario);
    List<Registro> findAll();
    List<Registro> findByUsuarioAndHoraEntradaBetween(Usuario usuario, LocalDateTime inicio, LocalDateTime fin);
    Optional<Registro> findById(Long id);
    Optional<Registro> findActiveByUsuarioId(Long usuarioId);
    List<Registro> findByUsuarioIdAndHoraEntradaBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
    void deleteById(Long id);
}
