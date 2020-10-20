package com.example.apiconsultorio.util.CustomModels;

public class RelatorioProfissional {
    private String profissionalName;
    private int profissionalId;
    private float totalValor;
    private float faltanteValor;
    private int totalConsultas;
    private int consultasNaoPaga;

    public String getProfissionalName() {
        return profissionalName;
    }

    public void setProfissionalName(String profissionalName) {
        this.profissionalName = profissionalName;
    }

    public int getProfissionalId() {
        return profissionalId;
    }

    public void setProfissionalId(int profissionalId) {
        this.profissionalId = profissionalId;
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
