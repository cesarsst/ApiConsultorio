package com.example.apiconsultorio.service;

import com.example.apiconsultorio.util.error.CustomErrorType;
import com.example.apiconsultorio.util.error.ResourceNotFoundException;
import com.example.apiconsultorio.util.error.ValidateAtributesException;
import com.example.apiconsultorio.model.Individuo;
import com.example.apiconsultorio.dao.IndividuoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/")
public class IndividuoService {

    private final IndividuoRepository individuoDAO;

    @Autowired
    public IndividuoService(IndividuoRepository individuoDAO) {
        this.individuoDAO = individuoDAO;
    }

    @GetMapping(path = "aux/individuo")
    public ResponseEntity<?> listAll(){
        return new ResponseEntity<>(individuoDAO.findAll(), HttpStatus.OK);
    }

    @GetMapping(path = "individuo/{id}")
    public ResponseEntity<?> getIndividuoById(@PathVariable("id") int id){
        verifyIfIndividuoExist(id);
        Individuo individuo = individuoDAO.findById(id);
        return new ResponseEntity<>(individuo, HttpStatus.OK);
    }

    @GetMapping(path="/aux/individuo/findByName/{nome}")
    public ResponseEntity<?> findIndividuoByName(@PathVariable String nome){
        List<Individuo> individuoList = individuoDAO.findByNomeIgnoreCaseContaining(nome);
        if(individuoList.size() == 0){
            throw new ResourceNotFoundException("Não foi possível encontrar algum indíviduo com este nome!");
        }else{
            return new ResponseEntity<>(individuoList, HttpStatus.OK);
        }
    }

    @PostMapping(path="/aux/newIndividuo")
    public ResponseEntity<?> save(@Validated @RequestBody Individuo individuo){
        validateAtributes(individuo);
        return new ResponseEntity<>(individuoDAO.save(individuo), HttpStatus.OK);
    }

    @DeleteMapping("/admin/individuo/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id){
        verifyIfIndividuoExist(id);
        individuoDAO.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/aux/individuo")
    public ResponseEntity<?> update(@RequestBody Individuo individuo){
        verifyIfIndividuoExist(individuo.getId());
        individuoDAO.save(individuo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void verifyIfIndividuoExist(int id){
        Individuo individuo = individuoDAO.findById(id);
        if(individuo == null){
            throw new ResourceNotFoundException("Individuo não encontrado com ID:"+id);
        }
    }

    private void validateAtributes(Individuo individuo){
        if(individuo.getNome() == "" || individuo.getNome() == null)
            throw new ValidateAtributesException("Atributo 'nome' não pode ser nulo!");
    }

}
