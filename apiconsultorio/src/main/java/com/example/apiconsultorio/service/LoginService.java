package com.example.apiconsultorio.service;

import com.example.apiconsultorio.dao.LoginRepository;
import com.example.apiconsultorio.dao.UsuarioRepository;
import com.example.apiconsultorio.model.Login;
import com.example.apiconsultorio.model.NewLogin;
import com.example.apiconsultorio.model.Usuario;
import com.example.apiconsultorio.util.error.ValidateAtributesException;
import com.example.apiconsultorio.util.passwordEncoder.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/")
public class LoginService {
    private final LoginRepository loginRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder = new PasswordEncoder();

    @Autowired
    public LoginService(LoginRepository loginRepository, UsuarioRepository usuarioRepository) {
        this.loginRepository = loginRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@AuthenticationPrincipal UserDetails userDetails){


        NewLogin newLogin = new NewLogin();
        Login login = loginRepository.findByUsername(userDetails.getUsername());
        newLogin.setUsername(login.getUsername());
        newLogin.setUsuarioId(login.getUsuarioId());

        Usuario usuario = usuarioRepository.findByUsuarioId(newLogin.getUsuarioId());
        newLogin.setRole(usuario.getCateg());

        return new ResponseEntity<>(newLogin, HttpStatus.OK);
    }


    private void validateAtributes(Login login){
        if(login.getUsername() == "" || login.getUsername() == null){
            throw new ValidateAtributesException("Atributo 'username' não pode ser nulo!");
        }else if(login.getPassword() == "" || login.getPassword() == null){
            throw new ValidateAtributesException("Atributo 'password' não pode ser nulo!");
        }
    }
}
