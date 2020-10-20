package com.example.apiconsultorio.service;

import com.example.apiconsultorio.dao.IndividuoRepository;
import com.example.apiconsultorio.dao.LoginRepository;
import com.example.apiconsultorio.dao.UsuarioRepository;
import com.example.apiconsultorio.model.Individuo;
import com.example.apiconsultorio.model.Login;
import com.example.apiconsultorio.model.NewAuxiliar;
import com.example.apiconsultorio.model.Usuario;
import com.example.apiconsultorio.util.error.ResourceNotFoundException;
import com.example.apiconsultorio.util.error.ValidateAtributesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("v1/")
public class GerenciadorService {

    private final IndividuoRepository individuoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LoginRepository loginRepository;
    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public GerenciadorService(IndividuoRepository individuoRepository, UsuarioRepository usuarioRepository, LoginRepository loginRepository) {
        this.individuoRepository = individuoRepository;
        this.usuarioRepository = usuarioRepository;
        this.loginRepository = loginRepository;
    }

    @PostMapping("/admin/newUser")
    @Transactional
    public ResponseEntity<?> newUser(@Validated @RequestBody NewAuxiliar newAuxiliar){
        verificAllAtributesToRegister(newAuxiliar);
        verificUsername(newAuxiliar.getUsername());

        Individuo individuo = new Individuo();
        individuo.setNome(newAuxiliar.getNome());
        individuo = individuoRepository.save(individuo);
        newAuxiliar.setId(individuo.getId());

        Usuario usuario = new Usuario();
        usuario.setUsuarioId(individuo.getId());
        usuario.setCateg(newAuxiliar.getCateg());
        usuarioRepository.save(usuario);

        Login login = new Login();
        login.setUsuarioId(individuo.getId());
        login.setUsername(newAuxiliar.getUsername());
        String passwordEncode = passwordEncoder.encode(newAuxiliar.getPassword());
        login.setPassword(passwordEncode);
        loginRepository.save(login);

        return new ResponseEntity<>(newAuxiliar, HttpStatus.OK);
    }

    @PutMapping("/admin/updateUser")
    @Transactional
    public ResponseEntity<?> updateUser(@Validated @RequestBody NewAuxiliar newAuxiliar){
        verificAllAtributesToRegister(newAuxiliar);
        verificIfUserExist(newAuxiliar);
        Individuo individuo = individuoRepository.findByNome(newAuxiliar.getNome());

        Usuario usuario = usuarioRepository.findByUsuarioId(individuo.getId());
        usuario.setCateg(newAuxiliar.getCateg());
        usuarioRepository.save(usuario);

        Login login = loginRepository.findByUsuarioId(individuo.getId());
        login.setUsername(newAuxiliar.getUsername());
        String passwordEncode = passwordEncoder.encode(newAuxiliar.getPassword());
        login.setPassword(passwordEncode);
        loginRepository.save(login);

        return new ResponseEntity<>(newAuxiliar, HttpStatus.OK);
    }

    @GetMapping("/admin/getAllUser")
    public ResponseEntity<?> getAllUser(){
        ArrayList<NewAuxiliar> userList = new ArrayList<>();
        List<Usuario> findUser = usuarioRepository.findAll();


        for(int i=0; i<findUser.size(); i++){
            NewAuxiliar newAuxiliar = new NewAuxiliar();
            newAuxiliar.setCateg(findUser.get(i).getCateg());

            Individuo individuo = individuoRepository.findById(findUser.get(i).getUsuarioId());

            newAuxiliar.setId(individuo.getId());
            newAuxiliar.setNome(individuo.getNome());

            Login  login = loginRepository.findByUsuarioId(findUser.get(i).getUsuarioId());
            newAuxiliar.setUsername(login.getUsername());

            userList.add(newAuxiliar);
        }

        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/aux/getAllProfissional")
    public ResponseEntity<?> getAllProfissional(){
        ArrayList<NewAuxiliar> userList = new ArrayList<>();
        List<Usuario> findUser = usuarioRepository.findAll();

        for(int i=0; i<findUser.size(); i++){
            if(findUser.get(i).getCateg().equals("profissional")){

                NewAuxiliar newAuxiliar = new NewAuxiliar();
                newAuxiliar.setCateg(findUser.get(i).getCateg());

                Individuo individuo = individuoRepository.findById(findUser.get(i).getUsuarioId());
                newAuxiliar.setId(individuo.getId());
                newAuxiliar.setNome(individuo.getNome());

                Login  login = loginRepository.findByUsuarioId(findUser.get(i).getUsuarioId());
                newAuxiliar.setUsername(login.getUsername());

                userList.add(newAuxiliar);
            }

        }

        if(userList.size() == 0){
            throw new ResourceNotFoundException("Não existe profissionais cadastrados no sistema!");
        }

        return new ResponseEntity<>(userList, HttpStatus.OK);
    }



    public void verificAllAtributesToRegister(NewAuxiliar newAuxiliar){

        String categ = newAuxiliar.getCateg();
        if(newAuxiliar.getNome() == "" || newAuxiliar.getNome() == null)
            throw new ValidateAtributesException("Atributo 'nome' não pode ser nulo!");
        if(newAuxiliar.getCateg() == "" || newAuxiliar.getCateg() == null)
            throw new ValidateAtributesException("Atributo 'categ' não pode ser nulo!");
        if(newAuxiliar.getUsername() == "" || newAuxiliar.getUsername() == null)
            throw new ValidateAtributesException("Atributo 'username' não pode ser nulo!");
        if(newAuxiliar.getPassword() == "" || newAuxiliar.getPassword() == null)
            throw new ValidateAtributesException("Atributo 'password' não pode ser nulo!");
        if(!categ.equals("auxiliar") && !categ.equals("profissional") && !categ.equals("administrador") ){
            throw new ValidateAtributesException("Atributo 'categ' inválido. Categorias válidas: 'auxiliar', 'profissional' e 'administrador'");
        }
    }

    public void verificIfUserExist(NewAuxiliar newAuxiliar){
        Individuo individuo = individuoRepository.findByNome(newAuxiliar.getNome());
        if(individuo == null){
            throw new ResourceNotFoundException("Individuo não encontrado com o nome :"+ newAuxiliar.getNome());
        }
    }

    public void verificUsername(String username){
        Login login = loginRepository.findByUsername(username);
        if(login != null){
            throw new ValidateAtributesException("Username já utilizado!");
        }
    }

}
