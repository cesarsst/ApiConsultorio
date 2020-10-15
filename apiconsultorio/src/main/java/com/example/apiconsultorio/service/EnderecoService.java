package com.example.apiconsultorio.service;

import com.example.apiconsultorio.dao.EnderecoRepository;
import com.example.apiconsultorio.model.Endereco;
import com.example.apiconsultorio.model.Individuo;
import com.example.apiconsultorio.util.CustomModels.NewPaciente;
import com.example.apiconsultorio.util.error.ValidateAtributesException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/")
public class EnderecoService {
    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public Endereco save(Individuo individuo, NewPaciente data){

        verificAllAtributesToRegister(data);

        Endereco endereco = new Endereco();
        endereco.setCep(data.getCep());
        endereco.setNumero(Integer.parseInt(data.getNumero()));
        endereco.setBairro(data.getBairro());
        endereco.setCidade(data.getCidade());
        endereco.setEstado(data.getEstado());
        endereco.setComplemento(data.getComplemento());
        endereco.setPacienteId(individuo.getId());
        enderecoRepository.save(endereco);

        return endereco;
    }

    public Endereco update(Individuo individuo, NewPaciente newPaciente){
        boolean valid =  findEnderecoByIdPaciente(individuo.getId());
        if(!valid){
            throw new ValidateAtributesException("Não foi encontrato um contato com o id passado!");
        }else{
            Endereco endereco =  enderecoRepository.findByPacienteId(individuo.getId());
            endereco.setCep(newPaciente.getCep());
            endereco.setNumero(Integer.parseInt(newPaciente.getNumero()));
            endereco.setBairro(newPaciente.getBairro());
            endereco.setCidade(newPaciente.getCidade());
            endereco.setEstado(newPaciente.getEstado());
            endereco.setComplemento(newPaciente.getComplemento());
            enderecoRepository.save(endereco);
            return endereco;
        }
    }

    public boolean findEnderecoByIdPaciente(int id){
        Endereco endereco = enderecoRepository.findByPacienteId(id);
        if(endereco.getId() != 0){
            return true;
        }
        return false;
    }

    private void verificAllAtributesToRegister(NewPaciente endereco){

        if(endereco.getCep() == "" || endereco.getCep() == null)
            throw new ValidateAtributesException("Atributo 'cep' não pode ser nulo!");
        if(endereco.getNumero() == "" || endereco.getNumero() == null)
            throw new ValidateAtributesException("Atributo 'numero' não pode ser nulo!");
        if(endereco.getBairro() == "" || endereco.getBairro() == null)
            throw new ValidateAtributesException("Atributo 'bairro' não pode ser nulo!");
        if(endereco.getCidade() == "" || endereco.getCidade() == null)
            throw new ValidateAtributesException("Atributo 'cidade' não pode ser nulo!");
        if(endereco.getEstado() == "" || endereco.getEstado() == null)
            throw new ValidateAtributesException("Atributo 'estado' não pode ser nulo!");
    }

}
