package com.control.acceso.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Registro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    
    @Enumerated(EnumType.STRING)
    private TipoRegistro tipoRegistro;
    
    public enum TipoRegistro {
        AUTOMATICO,    // Cerrado por el scheduler
        MANUAL,        // Registrado con botones
        MANUAL_EDITADO // Registrado mediante formulario
    }
}
