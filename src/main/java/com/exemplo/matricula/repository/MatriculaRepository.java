package com.exemplo.matricula.repository;

import com.exemplo.matricula.model.Matricula;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MatriculaRepository extends MongoRepository<Matricula, String> {
    long countByCursoId(String cursoId);

    List<Matricula> findAllByCursoIdOrderByDataMatriculaAsc(String cursoId);

    boolean existsByAlunoId(String alunoId);

    boolean existsByCursoId(String cursoId);

    boolean existsByAlunoIdAndCursoId(String alunoId, String cursoId);

    boolean existsByAlunoIdAndCursoIdAndIdNot(String alunoId, String cursoId, String id);
}
