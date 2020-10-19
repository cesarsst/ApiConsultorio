package com.example.apiconsultorio.service;

import com.example.apiconsultorio.dao.LoginRepository;
import com.example.apiconsultorio.model.Login;
import com.example.apiconsultorio.model.NewLogin;
import com.example.apiconsultorio.util.error.ResourceNotFoundException;
import com.example.apiconsultorio.util.error.ValidateAtributesException;
import com.example.apiconsultorio.util.passwordEncoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/")
public class LoginService {
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder = new PasswordEncoder();

    @Autowired
    public LoginService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody Login login){
        validateAtributes(login);
        Login findLogin = loginRepository.findByUsername(login.getUsername());


        if(findLogin == null){
            throw new ResourceNotFoundException("Usuário não encontrado!");
        }

        boolean validate = new PasswordEncoder().comparePassword(login.getPassword(), findLogin.getPassword());
        if(!validate){
            throw new ValidateAtributesException("Senha incorreta!");
        }

        NewLogin newLogin = new NewLogin();

        return new ResponseEntity<>(findLogin, HttpStatus.OK);
    }


    private void validateAtributes(Login login){
        if(login.getUsername() == "" || login.getUsername() == null){
            throw new ValidateAtributesException("Atributo 'username' não pode ser nulo!");
        }else if(login.getPassword() == "" || login.getPassword() == null){
            throw new ValidateAtributesException("Atributo 'password' não pode ser nulo!");
        }
    }
}
