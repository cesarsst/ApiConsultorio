package com.example.apiconsultorio.dao;

import com.example.apiconsultorio.model.Consulta;
import com.example.apiconsultorio.model.Individuo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConsultaRepository extends CrudRepository<Consulta, Integer> {
    List<Consulta> findByPacienteId(int id);
    List<Consulta> findByMedicoId(int id);
    Consulta findById(int id);
}
