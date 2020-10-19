package com.example.apiconsultorio.dao;

import com.example.apiconsultorio.model.Consulta;
import com.example.apiconsultorio.model.Pagamento;
import org.springframework.data.repository.CrudRepository;

public interface PagamentosRepository extends CrudRepository<Pagamento, Integer> {
    Pagamento findByConsultaId(int id);

}
