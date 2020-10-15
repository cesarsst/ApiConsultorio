package com.example.apiconsultorio.dao;

import com.example.apiconsultorio.model.Login;
import org.springframework.data.repository.CrudRepository;


public interface LoginRepository extends CrudRepository<Login, Integer> {
    Login findByUsername(String username);
}
