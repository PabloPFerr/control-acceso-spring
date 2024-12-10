package com.control.acceso.infrastructure.scheduler;

import com.control.acceso.domain.model.Registro;
import com.control.acceso.domain.port.incoming.RegistroManagementUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class RegistroScheduler {

    private final RegistroManagementUseCase registroManagementUseCase;

    @Autowired
    public RegistroScheduler(RegistroManagementUseCase registroManagementUseCase) {
        this.registroManagementUseCase = registroManagementUseCase;
    }

    @Scheduled(cron = "0 1 0 * * *") // Se ejecuta todos los días a las 00:01
    @Transactional
    public void cerrarRegistrosAbiertos() {
        LocalDateTime inicioDiaAnterior = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime finDiaAnterior = LocalDate.now().atStartOfDay();

        List<Registro> registrosAbiertos = registroManagementUseCase.obtenerRegistrosAbiertosEnPeriodo(inicioDiaAnterior, finDiaAnterior);

        for (Registro registro : registrosAbiertos) {
            // Establecer la hora de salida como 8 horas después de la entrada
            LocalDateTime horaSalida = registro.getHoraEntrada().plusHours(8);
            
            // Si la hora de salida calculada es después de las 23:59:59 del día anterior,
            // establecerla a esa hora
            LocalDateTime maxHoraSalida = finDiaAnterior.minusSeconds(1);
            if (horaSalida.isAfter(maxHoraSalida)) {
                horaSalida = maxHoraSalida;
            }

            registro.setHoraSalida(horaSalida);
            registro.setTipoRegistro(Registro.TipoRegistro.AUTOMATICO);
            registro.getUsuario().setTrabajando(false);
            registroManagementUseCase.actualizarRegistro(registro);
        }
    }
}
