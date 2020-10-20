package com.example.apiconsultorio.service;

import com.example.apiconsultorio.dao.*;
import com.example.apiconsultorio.model.*;

import com.example.apiconsultorio.model.NewConsulta;
import com.example.apiconsultorio.model.UpdateConsulta;
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
import java.util.ArrayList;
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
        }

        List<UpdateConsulta> returnList = new ArrayList<>();
        for(int i=0; i<consultaList.size(); i++){
            Consulta consulta = consultaList.get(i);
            UpdateConsulta updateConsulta = new UpdateConsulta();

            updateConsulta.setId(consulta.getId());
            updateConsulta.setConcluida(consulta.isConcluida());
            updateConsulta.setMedicoId(consulta.getMedicoId());
            updateConsulta.setPacienteId(consulta.getPacienteId());

            LocalDateTime currentDateTime = consulta.getData();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String data = currentDateTime.format(formatter);
            updateConsulta.setData(data);

            Pagamento pagamento = pagamentosRepository.findByConsultaId(consulta.getId());
            updateConsulta.setQuitado(pagamento.isQuitado());
            updateConsulta.setResumo(pagamento.getResumo());
            updateConsulta.setValor(pagamento.getValor());

            Individuo individuo = individuoRepository.findById(consulta.getPacienteId());
            updateConsulta.setPacienteName(individuo.getNome());

            returnList.add(updateConsulta);

        }


        return new ResponseEntity<>(returnList, HttpStatus.OK);

    }

    @GetMapping("/aux/findConsultaByPacienteId/{id}")
    public ResponseEntity<?> findConsultaByPacienteId(@PathVariable int id){
        verificPacienteId(id);
        List<Consulta> consultaList = consultaRepository.findByPacienteId(id);
        if(consultaList.size() == 0){
            throw new ResourceNotFoundException("Não existe consultas marcadas para este paciente!");
        }

        List<UpdateConsulta> returnList = new ArrayList<>();
        for(int i=0; i<consultaList.size(); i++){
            Consulta consulta = consultaList.get(i);
            UpdateConsulta updateConsulta = new UpdateConsulta();

            updateConsulta.setId(consulta.getId());
            updateConsulta.setConcluida(consulta.isConcluida());
            updateConsulta.setMedicoId(consulta.getMedicoId());
            updateConsulta.setPacienteId(consulta.getPacienteId());

            LocalDateTime currentDateTime = consulta.getData();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String data = currentDateTime.format(formatter);
            updateConsulta.setData(data);

            Pagamento pagamento = pagamentosRepository.findByConsultaId(consulta.getId());
            updateConsulta.setQuitado(pagamento.isQuitado());
            updateConsulta.setResumo(pagamento.getResumo());
            updateConsulta.setValor(pagamento.getValor());

            Individuo individuo = individuoRepository.findById(consulta.getPacienteId());
            updateConsulta.setPacienteName(individuo.getNome());

            returnList.add(updateConsulta);

        }


        return new ResponseEntity<>(returnList, HttpStatus.OK);
    }

    @GetMapping("/aux/findConsultaByProfissional/{id}")
    public ResponseEntity<?> findConsultaByProfissional(@PathVariable int id){
        verificProfissionalId(id);
        List<Consulta> consultaList = consultaRepository.findByMedicoId(id);
        if(consultaList.size() == 0){
            throw new ResourceNotFoundException("Não existe consultas marcadas para este profissional!");
        }

        List<UpdateConsulta> returnList = new ArrayList<>();
        for(int i=0; i<consultaList.size(); i++){
            Consulta consulta = consultaList.get(i);
            UpdateConsulta updateConsulta = new UpdateConsulta();

            updateConsulta.setId(consulta.getId());
            updateConsulta.setConcluida(consulta.isConcluida());
            updateConsulta.setMedicoId(consulta.getMedicoId());
            updateConsulta.setPacienteId(consulta.getPacienteId());

            LocalDateTime currentDateTime = consulta.getData();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String data = currentDateTime.format(formatter);
            updateConsulta.setData(data);

            Pagamento pagamento = pagamentosRepository.findByConsultaId(consulta.getId());
            updateConsulta.setQuitado(pagamento.isQuitado());
            updateConsulta.setResumo(pagamento.getResumo());
            updateConsulta.setValor(pagamento.getValor());

            Individuo individuo = individuoRepository.findById(consulta.getPacienteId());
            updateConsulta.setPacienteName(individuo.getNome());

            returnList.add(updateConsulta);

        }

        return new ResponseEntity<>(returnList, HttpStatus.OK);

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

        List<UpdateConsulta> returnList = new ArrayList<>();
        for(int i=0; i<consultaList.size(); i++){
            Consulta consulta = consultaList.get(i);
            UpdateConsulta updateConsulta = new UpdateConsulta();

            updateConsulta.setId(consulta.getId());
            updateConsulta.setConcluida(consulta.isConcluida());
            updateConsulta.setMedicoId(consulta.getMedicoId());
            updateConsulta.setPacienteId(consulta.getPacienteId());

            LocalDateTime currentDateTime = consulta.getData();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String data = currentDateTime.format(formatter);
            updateConsulta.setData(data);

            Pagamento pagamento = pagamentosRepository.findByConsultaId(consulta.getId());
            updateConsulta.setQuitado(pagamento.isQuitado());
            updateConsulta.setResumo(pagamento.getResumo());
            updateConsulta.setValor(pagamento.getValor());

            Individuo individuoPaciente = individuoRepository.findById(consulta.getPacienteId());
            updateConsulta.setPacienteName(individuoPaciente.getNome());

            returnList.add(updateConsulta);

        }

        return new ResponseEntity<>(returnList, HttpStatus.OK);
    }

    @PutMapping("/aux/updateConsulta")
    @Transactional
    public ResponseEntity<?> update(@Validated @RequestBody UpdateConsulta updateConsulta){
        verificUpdate(updateConsulta);
        verificPacienteId(updateConsulta.getPacienteId());
        verficiUsuarioId(updateConsulta.getMedicoId());

        Consulta consulta = consultaRepository.findById(updateConsulta.getId());

        consulta.setConcluida(updateConsulta.isConcluida());
        consulta.setMedicoId(updateConsulta.getMedicoId());
        consulta.setPacienteId(updateConsulta.getPacienteId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(updateConsulta.getData(), formatter);
        consulta.setData(dateTime);
        consultaRepository.save(consulta);

        Pagamento  pagamento = pagamentosRepository.findByConsultaId(updateConsulta.getId());
        pagamento.setQuitado(updateConsulta.isQuitado());
        pagamento.setResumo(updateConsulta.getResumo());
        pagamento.setValor(updateConsulta.getValor());
        pagamentosRepository.save(pagamento);

        Individuo  individuo = individuoRepository.findById(updateConsulta.getPacienteId());
        updateConsulta.setPacienteName(individuo.getNome());

        return new ResponseEntity<>(updateConsulta, HttpStatus.OK);
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
        if(newConsulta.getMedicoId() == 0 || Integer.toString(newConsulta.getMedicoId()) == null){
            throw new ValidateAtributesException("Atributo 'medicoId' não pode ser nulo!");
        }
        if(newConsulta.getPacienteId() == 0 || Integer.toString(newConsulta.getPacienteId()) == null){
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

    public void verificUpdate(UpdateConsulta updateConsulta){
        Consulta consulta = consultaRepository.findById(updateConsulta.getId());
        if(consulta == null){
            throw new ResourceNotFoundException("Consulta não encontrada com ID:"+updateConsulta.getId());
        }

        if(updateConsulta.getMedicoId() == 0 || Integer.toString(updateConsulta.getMedicoId()) == null){
            throw new ValidateAtributesException("Atributo 'medicoId' não pode ser nulo!");
        }
        if(updateConsulta.getPacienteId() == 0 || Integer.toString(updateConsulta.getPacienteId()) == null){
            throw new ValidateAtributesException("Atributo 'pacienteId' não pode ser nulo!");
        }
        if(updateConsulta.getData() == null){
            throw new ValidateAtributesException("Atributo 'data' não pode ser nulo!");
        }
        if(Float.toString(updateConsulta.getValor()) == null){
            throw new ValidateAtributesException("Atributo 'valor' não pode ser nulo!");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try{
            LocalDateTime dateTime = LocalDateTime.parse(updateConsulta.getData(), formatter);
        } catch (DateTimeParseException e){
            throw new ValidateAtributesException("Atributo 'data' inválido!");
        }

    }

}
