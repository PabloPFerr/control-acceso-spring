package com.control.acceso.interfaces.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class RegistroManualDTO {
    private LocalDate fechaEntrada;
    private LocalTime horaEntrada;
    private LocalDate fechaSalida;
    private LocalTime horaSalida;

    public LocalDateTime getEntradaDateTime() {
        return LocalDateTime.of(fechaEntrada, horaEntrada);
    }

    public LocalDateTime getSalidaDateTime() {
        return LocalDateTime.of(fechaSalida, horaSalida);
    }
}
