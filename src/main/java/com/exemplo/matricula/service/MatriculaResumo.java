package com.exemplo.matricula.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MatriculaResumo {

    private final String id;
    private final String alunoId;
    private final String alunoNome;
    private final String cursoId;
    private final String cursoNome;
    private final LocalDate dataMatricula;

    public MatriculaResumo(String id, String alunoId, String alunoNome, String cursoId,
                           String cursoNome, LocalDate dataMatricula) {
        this.id = id;
        this.alunoId = alunoId;
        this.alunoNome = alunoNome;
        this.cursoId = cursoId;
        this.cursoNome = cursoNome;
        this.dataMatricula = dataMatricula;
    }

    public String getId() {
        return id;
    }

    public String getAlunoId() {
        return alunoId;
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public String getCursoId() {
        return cursoId;
    }

    public String getCursoNome() {
        return cursoNome;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public String getDataMatriculaFormatada() {
        return dataMatricula == null ? "—" : dataMatricula.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
