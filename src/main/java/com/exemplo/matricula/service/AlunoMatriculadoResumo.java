package com.exemplo.matricula.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AlunoMatriculadoResumo {

    private final String matriculaId;
    private final String nome;
    private final String cpf;
    private final String email;
    private final LocalDate dataMatricula;

    public AlunoMatriculadoResumo(String matriculaId, String nome, String cpf, String email, LocalDate dataMatricula) {
        this.matriculaId = matriculaId;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.dataMatricula = dataMatricula;
    }

    public String getMatriculaId() {
        return matriculaId;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getDataMatriculaFormatada() {
        return dataMatricula == null ? "—" : dataMatricula.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
