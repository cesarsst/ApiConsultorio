package com.example.apiconsultorio.service;

import com.example.apiconsultorio.dao.IndividuoRepository;
import com.example.apiconsultorio.dao.PacienteRepository;
import com.example.apiconsultorio.model.Individuo;
import com.example.apiconsultorio.model.Paciente;
import com.example.apiconsultorio.util.CustomModels.NewPaciente;
import com.example.apiconsultorio.util.error.ResourceNotFoundException;
import com.example.apiconsultorio.util.error.ValidateAtributesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("v1/")
public class PacienteService {

    private final IndividuoRepository individuoRepository;
    private final PacienteRepository pacienteRepository;
    private final EnderecoService enderecoService;
    private final ContatoService contatoService;

    @Autowired
    public PacienteService(IndividuoRepository individuoRepository, PacienteRepository pacienteRepository, EnderecoService enderecoService, ContatoService contatoService) {
        this.individuoRepository = individuoRepository;
        this.pacienteRepository = pacienteRepository;
        this.enderecoService = enderecoService;
        this.contatoService = contatoService;
    }

    @PostMapping("/aux/newPaciente")
    @Transactional
    public ResponseEntity<?> save(@Validated @RequestBody NewPaciente newPaciente){
        verificAllAtributesToRegister(newPaciente);

        Individuo individuo = new Individuo(newPaciente.getNome());
        individuo = individuoRepository.save(individuo);
        newPaciente.setId(individuo.getId());

        Paciente paciente = new Paciente();
        paciente.setPacienteId(individuo.getId());
        paciente.setCpf(newPaciente.getCpf());
        pacienteRepository.save(paciente);

        contatoService.save(individuo, newPaciente);
        enderecoService.save(individuo, newPaciente);

        return  new ResponseEntity<>(newPaciente, HttpStatus.OK);
    }

    @GetMapping("/aux/findPacienteByName/{nome}")
    public ResponseEntity<?> findPacienteByName(@PathVariable String nome){
        List<Individuo> individuoList = individuoRepository.findByNomeIgnoreCaseContaining(nome);
        if(individuoList.size() == 0 || (nome == "" || nome == null)){
            throw new ResourceNotFoundException("Não foi possível encontrar algum indíviduo com este nome!");
        }else{
            return new ResponseEntity<>(individuoList, HttpStatus.OK);
        }
    }

    @PutMapping("/aux/updatePaciente")
    @Transactional
    public ResponseEntity<?> update(@Validated @RequestBody NewPaciente newPaciente){
        verificUpdate(newPaciente.getId());

        Individuo individuo = new Individuo();
        individuo.setId(newPaciente.getId());
        individuo.setNome(newPaciente.getNome());
        individuoRepository.save(individuo);

        contatoService.update(individuo, newPaciente);
        enderecoService.update(individuo, newPaciente);

        return  new ResponseEntity<>(newPaciente, HttpStatus.OK);
    }

    @DeleteMapping("/admin/deletePaciente/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id){
        verificUpdate(id);
        individuoRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void verificAllAtributesToRegister(NewPaciente newPaciente){
        if(newPaciente.getNome() == "" || newPaciente.getNome() == null)
            throw new ValidateAtributesException("Atributo 'nome' não pode ser nulo!");

        if(newPaciente.getCpf() == "" || newPaciente.getCpf() == null){
            throw new ValidateAtributesException("Atributo 'cpf' não pode ser nulo!");
        }else{
            List<Paciente> pacienteExist = pacienteRepository.findByCpf(newPaciente.getCpf());
            if(pacienteExist.size() > 0){
                throw new ValidateAtributesException("Já existe um paciente cadastrado com esse cpf!");
            }
        }

    }

    public void verificUpdate(int id){
        if(!individuoRepository.findById(id).isPresent()){
            throw new ResourceNotFoundException("Individuo não encontrado com ID:"+id);
        }
    }
}
