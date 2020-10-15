package com.example.apiconsultorio.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Paciente {
    @Id
    private int pacienteId;
    private String cpf;

    public Paciente(int paciente_id, String cpf) {
        this.pacienteId = paciente_id;
        this.cpf = cpf;
    }

    public Paciente() {
    }

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
