package com.example.apiconsultorio.service;

import com.example.apiconsultorio.dao.ContatoRepository;
import com.example.apiconsultorio.model.Contato;
import com.example.apiconsultorio.model.Individuo;
import com.example.apiconsultorio.util.CustomModels.NewPaciente;
import com.example.apiconsultorio.util.error.ValidateAtributesException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/")
public class ContatoService {

    private final ContatoRepository contatoRepository;

    public ContatoService(ContatoRepository contatoRepository) {
        this.contatoRepository = contatoRepository;
    }

    /*
    * Realiza o cadastro de um novo endereço referente ao indiviuo passado
    * */
    public Contato save(Individuo individuo, NewPaciente data){

        verificAllAtributesToRegister(data);

        Contato contato = new Contato();
        contato.setPacienteId(individuo.getId());
        contato.setEmail(data.getEmail());
        contato.setTelefone(data.getTelefone());
        contato.setWhatsapp(data.getWhatsapp());
        contato = contatoRepository.save(contato);

        return contato;
    }


    public Contato update(Individuo individuo, NewPaciente data){
         boolean valid = findContatoByIdPaciente(individuo.getId());
         if(!valid){
             throw new ValidateAtributesException("Não foi encontrato um contato com o id passado!");
         }else{
             Contato contato = contatoRepository.findByPacienteId(individuo.getId());
             contato.setEmail(data.getEmail());
             contato.setWhatsapp(data.getWhatsapp());
             contato.setTelefone(data.getTelefone());
             contatoRepository.save(contato);
             return contato;
         }

    }

    public boolean findContatoByIdPaciente(int id){
        Contato contato = contatoRepository.findByPacienteId(id);
        if(contato.getId() != 0){
            return true;
        }
        return false;
    }

    public boolean verificAllAtributesToRegister(NewPaciente contato){
        if(contato.getWhatsapp() == "" || contato.getWhatsapp() == null)
            throw new ValidateAtributesException("Atributo 'whatsapp' não pode ser nulo!");
        if(contato.getEmail() == "" || contato.getEmail() == null)
            throw new ValidateAtributesException("Atributo 'email' não pode ser nulo!");
        if(contato.getTelefone() == "" || contato.getTelefone() == null)
            throw new ValidateAtributesException("Atributo 'telefone' não pode ser nulo!");

        return true;
    }



}
