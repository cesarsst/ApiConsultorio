package com.example.apiconsultorio.dao;

import com.example.apiconsultorio.model.Individuo;
import com.example.apiconsultorio.model.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {
    Usuario findById(int usuarioId);
    List<Usuario> findAll();
}
