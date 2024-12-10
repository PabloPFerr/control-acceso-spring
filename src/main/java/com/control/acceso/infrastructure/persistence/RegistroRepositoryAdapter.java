package com.control.acceso.infrastructure.persistence;

import com.control.acceso.domain.model.Registro;
import com.control.acceso.domain.model.Usuario;
import com.control.acceso.domain.port.outgoing.RegistroRepositoryPort;
import com.control.acceso.infrastructure.repository.JpaRegistroRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class RegistroRepositoryAdapter implements RegistroRepositoryPort {

    private final JpaRegistroRepository registroRepository;

    public RegistroRepositoryAdapter(JpaRegistroRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    @Override
    public Registro save(Registro registro) {
        return registroRepository.save(registro);
    }

    @Override
    public Optional<Registro> findById(Long id) {
        return registroRepository.findById(id);
    }

    @Override
    public Optional<Registro> findActiveByUsuarioId(Long usuarioId) {
        return registroRepository.findByUsuarioIdAndHoraSalidaIsNull(usuarioId);
    }

    @Override
    public List<Registro> findByHoraEntradaBetween(LocalDateTime inicio, LocalDateTime fin) {
        return registroRepository.findByHoraEntradaBetweenOrderByHoraEntradaDesc(inicio, fin);
    }

    @Override
    public List<Registro> findByUsuarioIdAndHoraEntradaBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin) {
        return registroRepository.findByUsuarioIdAndHoraEntradaBetweenOrderByHoraEntradaDesc(usuarioId, inicio, fin);
    }

    @Override
    public void deleteById(Long id) {
        registroRepository.deleteById(id);
    }

    @Override
    public List<Registro> findByUsuarioOrderByHoraEntradaDesc(Usuario usuario) {
        return registroRepository.findByUsuarioOrderByHoraEntradaDesc(usuario);
    }

    @Override
    public List<Registro> findByHoraSalidaIsNullAndHoraEntradaBetween(LocalDateTime inicio, LocalDateTime fin) {
        return registroRepository.findByHoraSalidaIsNullAndHoraEntradaBetween(inicio, fin);
    }

    @Override
    public Optional<Registro> findFirstByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(Usuario usuario) {
        return registroRepository.findFirstByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(usuario);
    }

    @Override
    public boolean existsByUsuarioAndHoraSalidaIsNull(Usuario usuario) {
        return registroRepository.existsByUsuarioAndHoraSalidaIsNull(usuario);
    }

    @Override
    public List<Registro> findAll() {
        return registroRepository.findAll();
    }

    @Override
    public List<Registro> findByUsuarioAndHoraEntradaBetween(Usuario usuario, LocalDateTime inicio, LocalDateTime fin) {
        return registroRepository.findByUsuarioAndHoraEntradaBetweenOrderByHoraEntradaDesc(usuario, inicio, fin);
    }
}
