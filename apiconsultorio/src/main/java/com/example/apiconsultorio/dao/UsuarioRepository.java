package com.example.apiconsultorio.dao;

import com.example.apiconsultorio.model.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {
    Usuario findById(int usuarioId);
}
