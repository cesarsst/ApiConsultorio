package com.example.apiconsultorio.dao;

import com.example.apiconsultorio.model.Endereco;
import org.springframework.data.repository.CrudRepository;

public interface EnderecoRepository extends CrudRepository<Endereco, Integer> {
    Endereco findByPacienteId(int id);
}
