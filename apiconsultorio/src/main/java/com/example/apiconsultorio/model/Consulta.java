package com.example.apiconsultorio.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Consulta extends AbstractEntity{

    private boolean concluida;
    private int medicoId;
    private int pacienteId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime data;

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public int getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(int medicoId) {
        this.medicoId = medicoId;
    }

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
