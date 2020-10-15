package com.example.apiconsultorio.dao;

import com.example.apiconsultorio.model.Individuo;
import com.example.apiconsultorio.model.Paciente;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PacienteRepository extends CrudRepository<Paciente, Integer> {
    List<Paciente> findByCpf(String cpf);
}
