package com.example.apiconsultorio.service;

import com.example.apiconsultorio.dao.*;
import com.example.apiconsultorio.model.*;

import com.example.apiconsultorio.model.NewConsulta;
import com.example.apiconsultorio.util.error.ResourceNotFoundException;
import com.example.apiconsultorio.util.error.ValidateAtributesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("v1/")
public class ConsultaService {

    private final UsuarioRepository usuarioRepository;
    private final RelatorioRepository relatorioRepository;
    private final PacienteRepository pacienteRepository;
    private final PagamentosRepository pagamentosRepository;
    private final ConsultaRepository consultaRepository;
    private final IndividuoRepository individuoRepository;
    private final PagamentoService pagamentoService;
    private final LoginRepository loginRepository;

    @Autowired
    public ConsultaService(UsuarioRepository usuarioRepository, RelatorioRepository relatorioRepository, PacienteRepository pacienteRepository, PagamentosRepository pagamentosRepository, ConsultaRepository consultaRepository, IndividuoRepository individuoRepository, PagamentoService pagamentoService, LoginRepository loginRepository) {
        this.usuarioRepository = usuarioRepository;
        this.relatorioRepository = relatorioRepository;
        this.pacienteRepository = pacienteRepository;
        this.pagamentosRepository = pagamentosRepository;
        this.consultaRepository = consultaRepository;
        this.individuoRepository = individuoRepository;
        this.pagamentoService = pagamentoService;
        this.loginRepository = loginRepository;
    }

    @PostMapping("/aux/newConsulta")
    @Transactional
    public ResponseEntity<?> save(@Validated @RequestBody NewConsulta newConsulta){
        verificAllAtributesToRegister(newConsulta);
        verficiUsuarioId(newConsulta.getMedicoId());
        verificPacienteId(newConsulta.getPacienteId());

        Consulta consulta = new Consulta();
        consulta.setConcluida(false);
        consulta.setMedicoId(newConsulta.getMedicoId());
        consulta.setPacienteId(newConsulta.getPacienteId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(newConsulta.getData(), formatter);
        consulta.setData(dateTime);
        Consulta consultaSave = consultaRepository.save(consulta);

        // Registrando um novo pagamento referente a consulta
        boolean valid = pagamentoService.save(consultaSave);
        if(!valid){
            throw new ValidateAtributesException("Não foi possivel registrar a consulta. Verifique os campos digitados!");
        }

        return new ResponseEntity<>(consultaSave, HttpStatus.OK);
    }

    @GetMapping("/aux/findAllConsultas")
    public ResponseEntity<?> findAllConsultas(){
        List<Consulta> consultaList = consultaRepository.findAll();
        if(consultaList.size() == 0){
            throw new ResourceNotFoundException("Não existe consultas marcadas!");
        }else{
            return new ResponseEntity<>(consultaList, HttpStatus.OK);
        }
    }

    @GetMapping("/aux/findConsultaByPacienteId/{id}")
    public ResponseEntity<?> findConsultaByPacienteId(@PathVariable int id){
        verificPacienteId(id);
        List<Consulta> consultaList = consultaRepository.findByPacienteId(id);
        if(consultaList.size() == 0){
            throw new ResourceNotFoundException("Não existe consultas marcadas para este paciente!");
        }else{
            return new ResponseEntity<>(consultaList, HttpStatus.OK);
        }
    }

    @GetMapping("/aux/findConsultaByProfissional/{id}")
    public ResponseEntity<?> findConsultaByProfissional(@PathVariable int id){
        verificProfissionalId(id);
        List<Consulta> consultaList = consultaRepository.findByMedicoId(id);
        if(consultaList.size() == 0){
            throw new ResourceNotFoundException("Não existe consultas marcadas para este profissional!");
        }else{
            return new ResponseEntity<>(consultaList, HttpStatus.OK);
        }
    }

    @GetMapping("/prof/findConsultaByUser")
    public ResponseEntity<?> findConsultaByUser(@AuthenticationPrincipal UserDetails userDetails){
        String userName = userDetails.getUsername();
        Login login = loginRepository.findByUsername(userName);
        Individuo individuo = individuoRepository.findById(login.getUsuarioId());
        int idUser = individuo.getId();
        List<Consulta> consultaList = consultaRepository.findByMedicoId(idUser);
        if(consultaList.size() == 0){
            throw new ResourceNotFoundException("Não existe consultas pendentes!");
        }

        return new ResponseEntity<>(consultaList, HttpStatus.OK);
    }

    @PutMapping("/aux/updateConsulta")
    @Transactional
    public ResponseEntity<?> update(@Validated @RequestBody NewConsulta newConsulta){
        verificUpdate(newConsulta.getId());
        verificAllAtributesToRegister(newConsulta);
        verificPacienteId(newConsulta.getPacienteId());
        verficiUsuarioId(newConsulta.getMedicoId());

        Consulta consulta = consultaRepository.findById(newConsulta.getId());

        if(consulta.isConcluida()){
            throw new ValidateAtributesException("Você não pode alterar uma consulta já finalizada!");
        }

        consulta.setMedicoId(newConsulta.getMedicoId());
        consulta.setPacienteId(newConsulta.getPacienteId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(newConsulta.getData(), formatter);
        consulta.setData(dateTime);
        consulta.setConcluida(newConsulta.isConcluida());


        return new ResponseEntity<>(consulta, HttpStatus.OK);
    }

    @DeleteMapping("/admin/deleteConsultaById/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id){
        verificConsultaId(id);
        Pagamento pagamento = pagamentosRepository.findByConsultaId(id);
        pagamentosRepository.deleteById(pagamento.getId());
        consultaRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void verificAllAtributesToRegister(NewConsulta newConsulta){
        if(newConsulta.getMedicoId() == 0){
            throw new ValidateAtributesException("Atributo 'medicoId' não pode ser nulo!");
        }
        if(newConsulta.getPacienteId() == 0){
            throw new ValidateAtributesException("Atributo 'pacienteId' não pode ser nulo!");
        }
        if(newConsulta.getData() == null){
            throw new ValidateAtributesException("Atributo 'data' não pode ser nulo!");
        }
        if(Float.toString(newConsulta.getValor()) == null){
            throw new ValidateAtributesException("Atributo 'valor' não pode ser nulo!");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try{
            LocalDateTime dateTime = LocalDateTime.parse(newConsulta.getData(), formatter);
        } catch (DateTimeParseException e){
            throw new ValidateAtributesException("Atributo 'data' inválido!");
        }

    }

    public void verificConsultaId(int id){
        Consulta consulta = consultaRepository.findById(id);
        if(consulta == null){
            throw new ResourceNotFoundException("Consulta não encontrada com ID:"+ id);
        }
    }

    public void verificPacienteId(int id){

        if(!pacienteRepository.findById(id).isPresent()){
            throw new ResourceNotFoundException("Paciente não encontrado com ID:"+ id);
        }
    }

    public void verificProfissionalId(int id){
        Usuario usuario = usuarioRepository.findByUsuarioId(id);
        if(usuario == null || !usuario.getCateg().equals("profissional")){
            throw new ResourceNotFoundException("Profissional não encontrado com ID:"+ id);
        }

    }

    public void verficiUsuarioId(int id){
        Usuario userFind = usuarioRepository.findByUsuarioId(id);
        System.out.println(userFind.getCateg());
        if(userFind == null){
            throw new ResourceNotFoundException("Médico não encontrado com ID:"+ id);
        } else if (!userFind.getCateg().equals("profissional")){
            throw new ResourceNotFoundException("O id fornecido não se refere a um médico!");
        }
    }

    public void verificUpdate(int id){
        Consulta consulta = consultaRepository.findById(id);
        if(consulta == null){
            throw new ResourceNotFoundException("Consulta não encontrada com ID:"+id);
        }
    }

}
