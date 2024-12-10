package com.control.acceso.interfaces.dto;

import com.control.acceso.domain.model.Registro;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.Duration;

@Data
public class RegistroDTO {
    private Long id;
    private Long usuarioId;
    private String nombreUsuario;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private String tipoRegistro;
    private Double duracion;

    public static RegistroDTO fromRegistro(Registro registro) {
        RegistroDTO dto = new RegistroDTO();
        dto.setId(registro.getId());
        dto.setUsuarioId(registro.getUsuario().getId());
        dto.setNombreUsuario(registro.getUsuario().getNombre());
        dto.setHoraEntrada(registro.getHoraEntrada());
        dto.setHoraSalida(registro.getHoraSalida());
        
        if (registro.getHoraSalida() != null) {
            double horas = Duration.between(registro.getHoraEntrada(), registro.getHoraSalida()).toMinutes() / 60.0;
            dto.setDuracion(Math.round(horas * 100.0) / 100.0);
        }
        
        dto.setTipoRegistro(registro.getTipoRegistro().toString());
        return dto;
    }
}
