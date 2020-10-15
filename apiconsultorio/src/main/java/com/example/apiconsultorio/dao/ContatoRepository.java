package com.example.apiconsultorio.dao;

import com.example.apiconsultorio.model.Contato;
import org.springframework.data.repository.CrudRepository;

public interface ContatoRepository extends CrudRepository<Contato, Integer> {
    Contato findByPacienteId(int id);

}
