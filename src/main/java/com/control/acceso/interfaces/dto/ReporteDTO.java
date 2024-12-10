package com.control.acceso.interfaces.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReporteDTO {
    private List<RegistroDTO> registrosDetallados;
    private Map<LocalDate, Map<String, Double>> horasPorDia;
    private Map<String, Double> horasTotales;
    private Map<String, List<Double>> datosGraficoLineas;
    private List<LocalDate> fechasGrafico;

    // Getters y Setters
    public List<RegistroDTO> getRegistrosDetallados() {
        return registrosDetallados;
    }

    public void setRegistrosDetallados(List<RegistroDTO> registrosDetallados) {
        this.registrosDetallados = registrosDetallados;
    }

    public Map<LocalDate, Map<String, Double>> getHorasPorDia() {
        return horasPorDia;
    }

    public void setHorasPorDia(Map<LocalDate, Map<String, Double>> horasPorDia) {
        this.horasPorDia = horasPorDia;
    }

    public Map<String, Double> getHorasTotales() {
        return horasTotales;
    }

    public void setHorasTotales(Map<String, Double> horasTotales) {
        this.horasTotales = horasTotales;
    }

    public Map<String, List<Double>> getDatosGraficoLineas() {
        return datosGraficoLineas;
    }

    public void setDatosGraficoLineas(Map<String, List<Double>> datosGraficoLineas) {
        this.datosGraficoLineas = datosGraficoLineas;
    }

    public List<LocalDate> getFechasGrafico() {
        return fechasGrafico;
    }

    public void setFechasGrafico(List<LocalDate> fechasGrafico) {
        this.fechasGrafico = fechasGrafico;
    }
}
