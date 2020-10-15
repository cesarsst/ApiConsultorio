package com.example.apiconsultorio.service;

import com.example.apiconsultorio.dao.ConsultaRepository;
import com.example.apiconsultorio.dao.PagamentosRepository;
import com.example.apiconsultorio.model.Consulta;
import com.example.apiconsultorio.model.Pagamento;
import com.example.apiconsultorio.util.error.ResourceNotFoundException;
import com.example.apiconsultorio.util.error.ValidateAtributesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Optional;

@RestController
@RequestMapping("v1/")
public class PagamentoService {

    private final PagamentosRepository pagamentosRepository;


    @Autowired
    public PagamentoService(PagamentosRepository pagamentosRepository) {
        this.pagamentosRepository = pagamentosRepository;
    }

    public boolean save(Consulta consulta){
        Pagamento newPagamento = new Pagamento();

        newPagamento.setConsultaId(consulta.getId());
        newPagamento.setQuitado(false);
        newPagamento.setResumo("Sem resumo.");
        pagamentosRepository.save(newPagamento);

        return true;
    }

    @GetMapping("/aux/findPagamentoByConsultaId/{id}")
    public ResponseEntity<?> findPagamentoByConsultaId(@PathVariable int id){
        Pagamento pagamentoFind = pagamentosRepository.findByConsultaId(id);
        if(pagamentoFind == null){
            throw new ResourceNotFoundException("Não foi possível encontrar um pagamento com consulta ID igual a: " + id);
        }
        return new ResponseEntity<>(pagamentoFind, HttpStatus.OK);
    }

    @PutMapping("/aux/updatePagamento")
    @Transactional
    public ResponseEntity<?> update(@Validated @RequestBody Pagamento pagamento){
        verificAllAtributesToUpdate(pagamento);

        Pagamento pagamentoFind = pagamentosRepository.findByConsultaId(pagamento.getConsultaId());
        pagamentoFind.setValor(pagamento.getValor());
        pagamentoFind.setResumo(pagamento.getResumo());
        pagamentoFind.setQuitado(pagamento.isQuitado());

        return new ResponseEntity<>(pagamentoFind, HttpStatus.OK);
    }

    public void verificAllAtributesToUpdate(Pagamento pagamento){
        verificId(pagamento.getConsultaId());

        if( (Boolean.toString(pagamento.isQuitado()) != "true") && (Boolean.toString(pagamento.isQuitado()) != "false")){
            throw new ValidateAtributesException("Atributo 'quitado' inválido!");
        }
        if(Float.toString(pagamento.getValor()) == null){
            throw new ValidateAtributesException("Atributo 'valor' não pode ser nulo!");
        }
        if(!Float.class.isInstance(pagamento.getValor())){
            throw new ValidateAtributesException("Atributo 'valor' deve ser do tipo float!");
        }

    }

    public void verificId(int id){
        Pagamento pagamento = pagamentosRepository.findByConsultaId(id);
        if(pagamento == null){
            throw new ResourceNotFoundException("Pagamento não encontrado com ID:"+ id);
        }
    }


}
