package com.exemplo.matricula.config;

import com.exemplo.matricula.model.Aluno;
import com.exemplo.matricula.model.Curso;
import com.exemplo.matricula.repository.AlunoRepository;
import com.exemplo.matricula.repository.CursoRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class DadosIniciais implements ApplicationRunner {

    private static final int TOTAL_DEMONSTRATIVO = 5;

    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;

    public DadosIniciais(AlunoRepository alunoRepository, CursoRepository cursoRepository) {
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        popularAlunos();
        popularCursos();
    }

    private void popularAlunos() {
        List<Aluno> existentes = alunoRepository.findAll();
        Set<String> cpfs = new HashSet<>();
        existentes.forEach(aluno -> cpfs.add(normalizarCpf(aluno.getCpf())));
        long total = existentes.size();

        for (Aluno aluno : alunosDemonstrativos()) {
            if (total >= TOTAL_DEMONSTRATIVO) {
                break;
            }
            if (cpfs.add(aluno.getCpf())) {
                alunoRepository.save(aluno);
                total++;
            }
        }
    }

    private void popularCursos() {
        List<Curso> existentes = cursoRepository.findAll();
        Set<String> nomes = new HashSet<>();
        existentes.forEach(curso -> nomes.add(normalizarNome(curso.getNome())));
        long total = existentes.size();

        for (Curso curso : cursosDemonstrativos()) {
            if (total >= TOTAL_DEMONSTRATIVO) {
                break;
            }
            if (nomes.add(normalizarNome(curso.getNome()))) {
                cursoRepository.save(curso);
                total++;
            }
        }
    }

    private List<Aluno> alunosDemonstrativos() {
        return List.of(
                aluno("Ana Souza", "11111111111", "ana.souza@example.com"),
                aluno("Bruno Lima", "22222222222", "bruno.lima@example.com"),
                aluno("Camila Rocha", "33333333333", "camila.rocha@example.com"),
                aluno("Daniel Costa", "44444444444", "daniel.costa@example.com"),
                aluno("Elisa Martins", "55555555555", "elisa.martins@example.com")
        );
    }

    private List<Curso> cursosDemonstrativos() {
        return List.of(
                curso("Sistemas de Informação", 120, 30),
                curso("Engenharia de Software", 160, 25),
                curso("Banco de Dados", 80, 20),
                curso("Redes de Computadores", 100, 35),
                curso("Desenvolvimento Web", 120, 40)
        );
    }

    private Aluno aluno(String nome, String cpf, String email) {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setCpf(cpf);
        aluno.setEmail(email);
        return aluno;
    }

    private Curso curso(String nome, int cargaHoraria, int vagas) {
        Curso curso = new Curso();
        curso.setNome(nome);
        curso.setCargaHoraria(cargaHoraria);
        curso.setVagas(vagas);
        return curso;
    }

    private String normalizarNome(String nome) {
        return nome == null ? "" : nome.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarCpf(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("\\D", "");
    }
}
