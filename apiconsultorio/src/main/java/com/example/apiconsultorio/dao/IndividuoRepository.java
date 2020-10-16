package com.example.apiconsultorio.dao;

import com.example.apiconsultorio.model.Individuo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IndividuoRepository extends CrudRepository<Individuo, Integer> {
    List<Individuo> findByNomeIgnoreCaseContaining(String name);
    Individuo findByNome(String name);
    Individuo findById(int id);
}
