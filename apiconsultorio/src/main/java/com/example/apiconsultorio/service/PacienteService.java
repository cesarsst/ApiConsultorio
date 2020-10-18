package com.example.apiconsultorio.service;

import com.example.apiconsultorio.dao.ContatoRepository;
import com.example.apiconsultorio.dao.EnderecoRepository;
import com.example.apiconsultorio.dao.IndividuoRepository;
import com.example.apiconsultorio.dao.PacienteRepository;
import com.example.apiconsultorio.model.Contato;
import com.example.apiconsultorio.model.Endereco;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("v1/")
public class PacienteService {

    private final IndividuoRepository individuoRepository;
    private final PacienteRepository pacienteRepository;
    private final EnderecoService enderecoService;
    private final ContatoService contatoService;
    private final ContatoRepository contatoRepository;
    private final EnderecoRepository enderecoRepository;
    @Autowired
    public PacienteService(IndividuoRepository individuoRepository, PacienteRepository pacienteRepository, EnderecoService enderecoService, ContatoService contatoService, ContatoRepository contatoRepository, EnderecoRepository enderecoRepository) {
        this.individuoRepository = individuoRepository;
        this.pacienteRepository = pacienteRepository;
        this.enderecoService = enderecoService;
        this.contatoService = contatoService;
        this.contatoRepository = contatoRepository;
        this.enderecoRepository = enderecoRepository;
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
        }

        List<NewPaciente> listAllPacientes = new ArrayList<NewPaciente>();

        for(int n=0; n<individuoList.size(); n++){
            Individuo  individuo = individuoList.get(n);

            Paciente paciente = pacienteRepository.findByPacienteId(individuo.getId());
            if(paciente != null){
                NewPaciente newPaciente = new NewPaciente();

                newPaciente.setId(individuo.getId());
                newPaciente.setNome(individuo.getNome());

                newPaciente.setCpf(paciente.getCpf());

                Contato contato = contatoRepository.findByPacienteId(individuo.getId());
                newPaciente.setEmail(contato.getEmail());
                newPaciente.setTelefone(contato.getTelefone());
                newPaciente.setWhatsapp(contato.getWhatsapp());

                Endereco endereco = enderecoRepository.findByPacienteId(individuo.getId());
                newPaciente.setCep(endereco.getCep());
                newPaciente.setNumero(Integer.toString(endereco.getNumero()));
                newPaciente.setBairro(endereco.getBairro());
                newPaciente.setCidade(endereco.getCidade());
                newPaciente.setEstado(endereco.getEstado());
                newPaciente.setComplemento(endereco.getComplemento());

                listAllPacientes.add(newPaciente);
            }
        }

        if(listAllPacientes.size() == 0){
            throw new ResourceNotFoundException("Não foi possível encontrar um paciente com este nome!");
        }

        return new ResponseEntity<>(listAllPacientes, HttpStatus.OK);
    }


    @GetMapping("/aux/findAllPaciente")
    public ResponseEntity<?> findAllPaciente(){
        List<Individuo> individuoList = individuoRepository.findAll();
        if(individuoList.size() == 0){
            throw new ResourceNotFoundException("Não existe individuos cadastrados!");
        }

        List<NewPaciente> listAllPacientes = new ArrayList<NewPaciente>();

        for(int i=0; i<individuoList.size(); i++){

            int  individuoId = individuoList.get(i).getId();
            NewPaciente newPaciente = new NewPaciente();
            Paciente paciente = pacienteRepository.findByPacienteId(individuoId);
            if(paciente != null){
                newPaciente.setId(individuoId);
                newPaciente.setNome(individuoList.get(i).getNome());
                newPaciente.setCpf(paciente.getCpf());

                Contato contato = contatoRepository.findByPacienteId(individuoId);
                newPaciente.setEmail(contato.getEmail());
                newPaciente.setTelefone(contato.getTelefone());
                newPaciente.setWhatsapp(contato.getWhatsapp());

                Endereco endereco = enderecoRepository.findByPacienteId(individuoId);
                newPaciente.setCep(endereco.getCep());
                newPaciente.setNumero(Integer.toString(endereco.getNumero()));
                newPaciente.setBairro(endereco.getBairro());
                newPaciente.setCidade(endereco.getCidade());
                newPaciente.setEstado(endereco.getEstado());
                newPaciente.setComplemento(endereco.getComplemento());

                listAllPacientes.add(newPaciente);
            }

        }

        return new ResponseEntity<>(listAllPacientes, HttpStatus.OK);

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
        Individuo individuo = individuoRepository.findById(id);
        if(individuo == null){
            throw new ResourceNotFoundException("Individuo não encontrado com ID:"+id);
        }
    }
}
