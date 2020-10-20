package com.example.apiconsultorio.util.CustomModels;

public class RelatorioPaciente {
    private int idPaciente;
    private String pacienteNome;
    private float totalValor;
    private float faltanteValor;
    private int totalConsultas;
    private int consultasNaoPaga;

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getPacienteNome() {
        return pacienteNome;
    }

    public void setPacienteNome(String pacienteNome) {
        this.pacienteNome = pacienteNome;
    }

    public float getTotalValor() {
        return totalValor;
    }

    public void setTotalValor(float totalValor) {
        this.totalValor = totalValor;
    }

    public float getFaltanteValor() {
        return faltanteValor;
    }

    public void setFaltanteValor(float faltanteValor) {
        this.faltanteValor = faltanteValor;
    }

    public int getTotalConsultas() {
        return totalConsultas;
    }

    public void setTotalConsultas(int totalConsultas) {
        this.totalConsultas = totalConsultas;
    }

    public int getConsultasNaoPaga() {
        return consultasNaoPaga;
    }

    public void setConsultasNaoPaga(int consultasNaoPaga) {
        this.consultasNaoPaga = consultasNaoPaga;
    }
}
