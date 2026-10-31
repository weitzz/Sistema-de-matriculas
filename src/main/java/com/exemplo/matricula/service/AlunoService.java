package com.exemplo.matricula.service;

import com.exemplo.matricula.model.Aluno;
import com.exemplo.matricula.repository.AlunoRepository;
import com.exemplo.matricula.repository.MatriculaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;

    public AlunoService(AlunoRepository alunoRepository, MatriculaRepository matriculaRepository) {
        this.alunoRepository = alunoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    public List<Aluno> listar() {
        return alunoRepository.findAll();
    }

    public Aluno buscarPorId(String id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
    }

    public Aluno salvar(Aluno aluno) {
        aluno.setId(null);
        normalizarCpf(aluno);
        garantirCpfDisponivel(aluno.getCpf(), null);
        return alunoRepository.save(aluno);
    }

    public Aluno atualizar(String id, Aluno dados) {
        buscarPorId(id);
        normalizarCpf(dados);
        garantirCpfDisponivel(dados.getCpf(), id);
        dados.setId(id);
        return alunoRepository.save(dados);
    }

    public void excluir(String id) {
        buscarPorId(id);
        if (matriculaRepository.existsByAlunoId(id)) {
            throw new RegraNegocioException("Não é possível excluir um aluno que possui matrículas. Exclua as matrículas primeiro.");
        }
        alunoRepository.deleteById(id);
    }

    private void garantirCpfDisponivel(String cpf, String idAtual) {
        boolean duplicado = idAtual == null
                ? alunoRepository.findByCpf(cpf).isPresent()
                : alunoRepository.existsByCpfAndIdNot(cpf, idAtual);
        if (duplicado) {
            throw new RegraNegocioException("Já existe um aluno cadastrado com este CPF.");
        }
    }

    private void normalizarCpf(Aluno aluno) {
        aluno.setCpf(aluno.getCpf().replaceAll("\\D", ""));
    }
}
