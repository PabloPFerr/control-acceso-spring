package com.control.acceso.domain.port.incoming;

import com.control.acceso.domain.model.Registro;
import com.control.acceso.domain.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistroManagementUseCase {
    Registro registrarEntrada(Usuario usuario);
    Registro registrarSalida(Usuario usuario);
    Registro registrarManualEditado(Usuario usuario, LocalDateTime entrada, LocalDateTime salida);
    List<Registro> obtenerRegistrosUsuario(Usuario usuario);
    List<Registro> obtenerRegistrosPeriodo(LocalDateTime inicio, LocalDateTime fin);
    List<Registro> obtenerRegistrosDeHoy();
    List<Registro> obtenerTodosLosRegistros();
    Optional<Registro> obtenerUltimoRegistroAbierto(Usuario usuario);
    boolean tieneRegistroAbierto(Usuario usuario);
    List<Registro> obtenerRegistrosAbiertosEnPeriodo(LocalDateTime inicio, LocalDateTime fin);
    Registro actualizarRegistro(Registro registro);
}
