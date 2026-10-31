package com.exemplo.matricula.repository;

import com.exemplo.matricula.model.Curso;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CursoRepository extends MongoRepository<Curso, String> {
}
