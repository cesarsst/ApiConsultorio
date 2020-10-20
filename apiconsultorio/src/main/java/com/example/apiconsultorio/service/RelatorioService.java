package com.example.apiconsultorio.service;


import com.example.apiconsultorio.dao.*;
import com.example.apiconsultorio.model.*;
import com.example.apiconsultorio.util.CustomModels.RelatorioPaciente;
import com.example.apiconsultorio.util.CustomModels.RelatorioProfissional;
import com.example.apiconsultorio.util.error.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("v1/")
public class RelatorioService {

    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;
    private final IndividuoRepository individuoRepository;
    private final PagamentosRepository pagamentosRepository;
    private final PacienteRepository pacienteRepository;

    @Autowired
    public RelatorioService(ConsultaRepository consultaRepository, UsuarioRepository usuarioRepository, IndividuoRepository individuoRepository, PagamentosRepository pagamentosRepository, PacienteRepository pacienteRepository) {
        this.consultaRepository = consultaRepository;
        this.usuarioRepository = usuarioRepository;
        this.individuoRepository = individuoRepository;
        this.pagamentosRepository = pagamentosRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping("/aux/relatorioByProfissional")
    public ResponseEntity<?> getRelatorioByProfissinal(){

        List<RelatorioProfissional> relatorioList = new ArrayList<>();
        List<Usuario> profissionalList = usuarioRepository.findAll();

        for(int i=0; i<profissionalList.size(); i++){
            if(profissionalList.get(i).getCateg().equals("profissional")){

                RelatorioProfissional relatorioProfissional = new RelatorioProfissional();
                int idUsuario = profissionalList.get(i).getUsuarioId();
                Individuo individuo = individuoRepository.findById(idUsuario);

                // Tratando todas as consultas do profissional
                List<Consulta> consultaList = consultaRepository.findByMedicoId(idUsuario);
                int totalConsultas = consultaList.size();
                float totalValor =0;
                float faltanteValor = 0;
                int consultasNaoPaga = 0;

                for(int j=0; j<consultaList.size(); j++){
                    Consulta consultaI = consultaList.get(j);
                    Pagamento pagamentoI = pagamentosRepository.findByConsultaId(consultaI.getId());
                    totalValor += pagamentoI.getValor();

                    if(!pagamentoI.isQuitado()){
                        faltanteValor += pagamentoI.getValor();
                        consultasNaoPaga += 1;
                    }
                }

                relatorioProfissional.setProfissionalId(idUsuario);
                relatorioProfissional.setProfissionalName(individuo.getNome());
                relatorioProfissional.setTotalConsultas(totalConsultas);
                relatorioProfissional.setTotalValor(totalValor);
                relatorioProfissional.setFaltanteValor(faltanteValor);
                relatorioProfissional.setConsultasNaoPaga(consultasNaoPaga);

                relatorioList.add(relatorioProfissional);
            }
        }


        return new ResponseEntity<>(relatorioList, HttpStatus.OK);
    }


    @GetMapping("/aux/relatorioByPaciente/{id}")
    public ResponseEntity<?> relatorioByPaciente(@PathVariable int id){
        verificPaciente(id);
        Individuo individuo = individuoRepository.findById(id);
        Paciente paciente = pacienteRepository.findByPacienteId(individuo.getId());

        List<Consulta> consultaList = consultaRepository.findByPacienteId(individuo.getId());
        RelatorioPaciente relatorioPaciente = new RelatorioPaciente();

        int totalConsultas = consultaList.size();
        float totalValor =0;
        float faltanteValor = 0;
        int consultasNaoPaga = 0;

        for(int i=0; i<consultaList.size(); i++){
            Consulta consulta = consultaList.get(i);
            Pagamento pagamento = pagamentosRepository.findByConsultaId(consulta.getId());

            totalValor += pagamento.getValor();

            if(!pagamento.isQuitado()){
                faltanteValor += pagamento.getValor();
                consultasNaoPaga += 1;
            }

        }

        relatorioPaciente.setIdPaciente(id);
        relatorioPaciente.setPacienteNome(individuo.getNome());
        relatorioPaciente.setTotalConsultas(totalConsultas);
        relatorioPaciente.setTotalValor(totalValor);
        relatorioPaciente.setFaltanteValor(faltanteValor);
        relatorioPaciente.setConsultasNaoPaga(consultasNaoPaga);

        return new ResponseEntity<>(relatorioPaciente, HttpStatus.OK);
    }


    public void verificPaciente(int id){
        Individuo individuo = individuoRepository.findById(id);
        if(individuo == null){
            throw new ResourceNotFoundException("Nenhum individuo encontrado com id:"+ id);
        }

        Paciente paciente = pacienteRepository.findByPacienteId(individuo.getId());
        if(paciente == null){
            throw new ResourceNotFoundException("Nenhum paciente encontrado com id:"+ id);
        }
    }

}
