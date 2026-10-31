package com.exemplo.matricula.controller;

import com.exemplo.matricula.model.Curso;
import com.exemplo.matricula.service.CursoService;
import com.exemplo.matricula.service.MatriculaService;
import com.exemplo.matricula.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cursos")
public class CursoController {

    private final CursoService cursoService;
    private final MatriculaService matriculaService;

    public CursoController(CursoService cursoService, MatriculaService matriculaService) {
        this.cursoService = cursoService;
        this.matriculaService = matriculaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cursos", cursoService.listar());
        return "cursos/lista";
    }

    @GetMapping("/{id}")
    public String visualizarAlunos(@PathVariable String id, Model model) {
        Curso curso = cursoService.buscarPorId(id);
        var alunosMatriculados = matriculaService.listarAlunosDoCurso(id);
        model.addAttribute("curso", curso);
        model.addAttribute("alunosMatriculados", alunosMatriculados);
        model.addAttribute("totalMatriculados", alunosMatriculados.size());
        model.addAttribute("vagasDisponiveis", Math.max(0, curso.getVagas() - alunosMatriculados.size()));
        return "cursos/alunos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        prepararFormulario(model, new Curso(), "Novo curso", "Cadastrar curso");
        return "cursos/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("curso") Curso curso,
                         BindingResult resultado,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            prepararFormulario(model, curso, "Novo curso", "Cadastrar curso");
            return "cursos/formulario";
        }
        cursoService.salvar(curso);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Curso cadastrado com sucesso.");
        return "redirect:/cursos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        prepararFormulario(model, cursoService.buscarPorId(id), "Editar curso", "Salvar alterações");
        return "cursos/formulario";
    }

    @PostMapping("/{id}/atualizar")
    public String atualizar(@PathVariable String id,
                            @Valid @ModelAttribute("curso") Curso curso,
                            BindingResult resultado,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        curso.setId(id);
        if (resultado.hasErrors()) {
            prepararFormulario(model, curso, "Editar curso", "Salvar alterações");
            return "cursos/formulario";
        }
        try {
            cursoService.atualizar(id, curso);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Dados do curso atualizados.");
            return "redirect:/cursos";
        } catch (RegraNegocioException e) {
            resultado.reject("vagas.insuficientes", e.getMessage());
            prepararFormulario(model, curso, "Editar curso", "Salvar alterações");
            return "cursos/formulario";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            cursoService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Curso excluído com sucesso.");
        } catch (RegraNegocioException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/cursos";
    }

    private void prepararFormulario(Model model, Curso curso, String titulo, String textoBotao) {
        model.addAttribute("curso", curso);
        model.addAttribute("tituloFormulario", titulo);
        model.addAttribute("textoBotao", textoBotao);
    }
}
