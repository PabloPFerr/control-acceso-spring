package com.control.acceso.application.service;

import com.control.acceso.domain.model.Registro;
import com.control.acceso.domain.model.Usuario;
import com.control.acceso.domain.port.incoming.RegistroManagementUseCase;
import com.control.acceso.domain.port.outgoing.RegistroRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RegistroService implements RegistroManagementUseCase {

    private final RegistroRepositoryPort registroRepository;

    public RegistroService(RegistroRepositoryPort registroRepository) {
        this.registroRepository = registroRepository;
    }

    @Override
    @Transactional
    public Registro registrarEntrada(Usuario usuario) {
        if (tieneRegistroAbierto(usuario)) {
            throw new RuntimeException("Ya tiene un registro de entrada sin salida");
        }

        Registro registro = new Registro();
        registro.setUsuario(usuario);
        registro.setHoraEntrada(LocalDateTime.now());
        registro.setTipoRegistro(Registro.TipoRegistro.MANUAL);
        return registroRepository.save(registro);
    }

    @Override
    @Transactional
    public Registro registrarSalida(Usuario usuario) {
        Optional<Registro> ultimoRegistro = registroRepository.findFirstByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(usuario);
        if (ultimoRegistro.isEmpty()) {
            throw new RuntimeException("No hay un registro de entrada sin salida");
        }

        Registro registro = ultimoRegistro.get();
        registro.setHoraSalida(LocalDateTime.now());
        return registroRepository.save(registro);
    }

    @Override
    @Transactional
    public Registro registrarManualEditado(Usuario usuario, LocalDateTime entrada, LocalDateTime salida) {
        if (salida.isBefore(entrada)) {
            throw new RuntimeException("La fecha de salida debe ser posterior a la fecha de entrada");
        }

        List<Registro> registrosExistentes = registroRepository.findByUsuarioAndHoraEntradaBetween(
            usuario, 
            entrada.toLocalDate().atStartOfDay(), 
            salida.toLocalDate().plusDays(1).atStartOfDay()
        );

        for (Registro reg : registrosExistentes) {
            if (reg.getHoraSalida() != null) {
                if (entrada.isBefore(reg.getHoraSalida()) && salida.isAfter(reg.getHoraEntrada())) {
                    throw new RuntimeException("El periodo solicitado se solapa con un registro existente");
                }
            }
        }

        Registro registro = new Registro();
        registro.setUsuario(usuario);
        registro.setHoraEntrada(entrada);
        registro.setHoraSalida(salida);
        registro.setTipoRegistro(Registro.TipoRegistro.MANUAL_EDITADO);
        return registroRepository.save(registro);
    }

    @Override
    public List<Registro> obtenerRegistrosUsuario(Usuario usuario) {
        return registroRepository.findByUsuarioOrderByHoraEntradaDesc(usuario);
    }

    @Override
    public List<Registro> obtenerRegistrosPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return registroRepository.findByHoraEntradaBetween(inicio, fin);
    }

    @Override
    public List<Registro> obtenerRegistrosDeHoy() {
        LocalDateTime inicioDelDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finDelDia = inicioDelDia.plusDays(1);
        return registroRepository.findByHoraEntradaBetween(inicioDelDia, finDelDia);
    }

    @Override
    public List<Registro> obtenerTodosLosRegistros() {
        return registroRepository.findAll();
    }

    @Override
    public Optional<Registro> obtenerUltimoRegistroAbierto(Usuario usuario) {
        return registroRepository.findFirstByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(usuario);
    }

    @Override
    public boolean tieneRegistroAbierto(Usuario usuario) {
        return registroRepository.existsByUsuarioAndHoraSalidaIsNull(usuario);
    }

    @Override
    public List<Registro> obtenerRegistrosAbiertosEnPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return registroRepository.findByHoraSalidaIsNullAndHoraEntradaBetween(inicio, fin);
    }

    @Override
    public Registro actualizarRegistro(Registro registro) {
        return registroRepository.save(registro);
    }
}
