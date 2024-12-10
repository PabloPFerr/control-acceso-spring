package com.control.acceso.infrastructure.repository;

import com.control.acceso.domain.model.Registro;
import com.control.acceso.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaRegistroRepository extends JpaRepository<Registro, Long> {
    Optional<Registro> findByUsuarioIdAndHoraSalidaIsNull(Long usuarioId);
    
    List<Registro> findByHoraEntradaBetweenOrderByHoraEntradaDesc(LocalDateTime inicio, LocalDateTime fin);
    
    List<Registro> findByUsuarioIdAndHoraEntradaBetweenOrderByHoraEntradaDesc(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);
    
    List<Registro> findByUsuarioOrderByHoraEntradaDesc(Usuario usuario);
    
    List<Registro> findByHoraSalidaIsNullAndHoraEntradaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    Optional<Registro> findFirstByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(Usuario usuario);
    
    boolean existsByUsuarioAndHoraSalidaIsNull(Usuario usuario);
    
    List<Registro> findByUsuarioAndHoraEntradaBetweenOrderByHoraEntradaDesc(Usuario usuario, LocalDateTime inicio, LocalDateTime fin);
}
