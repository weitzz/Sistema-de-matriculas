package com.exemplo.matricula.repository;

import com.exemplo.matricula.model.Aluno;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AlunoRepository extends MongoRepository<Aluno, String> {
    Optional<Aluno> findByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, String id);
}
