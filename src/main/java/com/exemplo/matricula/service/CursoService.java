package com.exemplo.matricula.service;

import com.exemplo.matricula.model.Curso;
import com.exemplo.matricula.repository.CursoRepository;
import com.exemplo.matricula.repository.MatriculaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;

    public CursoService(CursoRepository cursoRepository, MatriculaRepository matriculaRepository) {
        this.cursoRepository = cursoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    public List<Curso> listar() {
        return cursoRepository.findAll();
    }

    public Curso buscarPorId(String id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado."));
    }

    public Curso salvar(Curso curso) {
        curso.setId(null);
        return cursoRepository.save(curso);
    }

    public Curso atualizar(String id, Curso dados) {
        buscarPorId(id);
        long matriculados = matriculaRepository.countByCursoId(id);
        if (dados.getVagas() < matriculados) {
            throw new RegraNegocioException("A quantidade de vagas não pode ser menor que o número de matrículas atuais (" + matriculados + ").");
        }
        dados.setId(id);
        return cursoRepository.save(dados);
    }

    public void excluir(String id) {
        buscarPorId(id);
        if (matriculaRepository.existsByCursoId(id)) {
            throw new RegraNegocioException("Não é possível excluir um curso com matrículas. Exclua as matrículas primeiro.");
        }
        cursoRepository.deleteById(id);
    }
}
