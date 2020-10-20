package com.example.apiconsultorio.service;


import com.example.apiconsultorio.dao.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/")
public class RelatorioService {

    private final ConsultaRepository consultaRepository;

    @Autowired
    public RelatorioService(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    @GetMapping("/aux/relatorioByProfissional")
    public ResponseEntity<?> getRelatorioByProfissinal(){
        return null;
    }

}
