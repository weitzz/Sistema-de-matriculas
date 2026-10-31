package com.exemplo.matricula.service;

import com.exemplo.matricula.model.Aluno;
import com.exemplo.matricula.model.Curso;
import com.exemplo.matricula.model.Matricula;
import com.exemplo.matricula.repository.AlunoRepository;
import com.exemplo.matricula.repository.CursoRepository;
import com.exemplo.matricula.repository.MatriculaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            AlunoRepository alunoRepository,
                            CursoRepository cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
    }

    public List<Matricula> listar() {
        return matriculaRepository.findAll();
    }

    public List<MatriculaResumo> listarResumos() {
        return matriculaRepository.findAll().stream()
                .map(matricula -> {
                    String alunoNome = alunoRepository.findById(matricula.getAlunoId())
                            .map(Aluno::getNome).orElse("Aluno removido");
                    String cursoNome = cursoRepository.findById(matricula.getCursoId())
                            .map(Curso::getNome).orElse("Curso removido");
                    return new MatriculaResumo(matricula.getId(), matricula.getAlunoId(), alunoNome,
                            matricula.getCursoId(), cursoNome, matricula.getDataMatricula());
                })
                .toList();
    }

    public List<AlunoMatriculadoResumo> listarAlunosDoCurso(String cursoId) {
        return matriculaRepository.findAllByCursoIdOrderByDataMatriculaAsc(cursoId).stream()
                .map(matricula -> {
                    Aluno aluno = alunoRepository.findById(matricula.getAlunoId()).orElse(null);
                    return new AlunoMatriculadoResumo(
                            matricula.getId(),
                            aluno == null ? "Aluno removido" : aluno.getNome(),
                            aluno == null ? "—" : aluno.getCpf(),
                            aluno == null ? "—" : aluno.getEmail(),
                            matricula.getDataMatricula());
                })
                .toList();
    }

    public Matricula buscarPorId(String id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matrícula não encontrada."));
    }

    public Matricula salvar(Matricula matricula) {
        matricula.setId(null);
        validarReferencias(matricula);
        validarDuplicidade(matricula, null);
        validarVagas(matricula.getCursoId());
        return matriculaRepository.save(matricula);
    }

    public Matricula atualizar(String id, Matricula dados) {
        Matricula atual = buscarPorId(id);
        validarReferencias(dados);
        validarDuplicidade(dados, id);
        if (!atual.getCursoId().equals(dados.getCursoId())) {
            validarVagas(dados.getCursoId());
        }
        dados.setId(id);
        return matriculaRepository.save(dados);
    }

    public void excluir(String id) {
        buscarPorId(id);
        matriculaRepository.deleteById(id);
    }

    private void validarReferencias(Matricula matricula) {
        if (!alunoRepository.existsById(matricula.getAlunoId())) {
            throw new RegraNegocioException("O aluno selecionado não existe mais.");
        }
        if (!cursoRepository.existsById(matricula.getCursoId())) {
            throw new RegraNegocioException("O curso selecionado não existe mais.");
        }
    }

    private void validarDuplicidade(Matricula matricula, String idAtual) {
        boolean duplicada = idAtual == null
                ? matriculaRepository.existsByAlunoIdAndCursoId(matricula.getAlunoId(), matricula.getCursoId())
                : matriculaRepository.existsByAlunoIdAndCursoIdAndIdNot(
                        matricula.getAlunoId(), matricula.getCursoId(), idAtual);
        if (duplicada) {
            throw new RegraNegocioException("Este aluno já está matriculado neste curso.");
        }
    }

    private void validarVagas(String cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RegraNegocioException("O curso selecionado não existe mais."));
        if (matriculaRepository.countByCursoId(cursoId) >= curso.getVagas()) {
            throw new RegraNegocioException("Este curso não possui vagas disponíveis.");
        }
    }
}
