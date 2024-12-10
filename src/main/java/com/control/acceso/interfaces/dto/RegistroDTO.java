package com.control.acceso.interfaces.dto;

import com.control.acceso.domain.model.Registro;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.Duration;

@Data
public class RegistroDTO {
    private Long id;
    private String usuario;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private String tipoRegistro;
    private String duracion;

    public static RegistroDTO fromRegistro(Registro registro) {
        RegistroDTO dto = new RegistroDTO();
        dto.setId(registro.getId());
        dto.setUsuario(registro.getUsuario().getNombre());
        dto.setHoraEntrada(registro.getHoraEntrada());
        dto.setHoraSalida(registro.getHoraSalida());
        
        if (registro.getHoraSalida() != null) {
            Duration duration = Duration.between(registro.getHoraEntrada(), registro.getHoraSalida());
            long horas = duration.toHours();
            long minutos = duration.toMinutesPart();
            dto.setDuracion(String.format("%d horas %d minutos", horas, minutos));
        }
        
        dto.setTipoRegistro(registro.getTipoRegistro().toString());
        return dto;
    }
}
