package com.example.apiconsultorio.model;

import lombok.NonNull;

import javax.persistence.Entity;


@Entity
public class Individuo extends AbstractEntity{

    @NonNull
    private String nome;

    public Individuo() {
    }

    public Individuo(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
