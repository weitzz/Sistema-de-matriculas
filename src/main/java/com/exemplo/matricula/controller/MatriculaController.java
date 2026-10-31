package com.exemplo.matricula.controller;

import com.exemplo.matricula.model.Matricula;
import com.exemplo.matricula.service.AlunoService;
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

import java.time.LocalDate;

@Controller
@RequestMapping("/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final AlunoService alunoService;
    private final CursoService cursoService;

    public MatriculaController(MatriculaService matriculaService,
                               AlunoService alunoService,
                               CursoService cursoService) {
        this.matriculaService = matriculaService;
        this.alunoService = alunoService;
        this.cursoService = cursoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("matriculas", matriculaService.listarResumos());
        return "matriculas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Matricula matricula = new Matricula();
        matricula.setDataMatricula(LocalDate.now());
        prepararFormulario(model, matricula, "Nova matrícula", "Registrar matrícula");
        return "matriculas/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("matricula") Matricula matricula,
                         BindingResult resultado,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            prepararFormulario(model, matricula, "Nova matrícula", "Registrar matrícula");
            return "matriculas/formulario";
        }
        try {
            matriculaService.salvar(matricula);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Matrícula registrada com sucesso.");
            return "redirect:/matriculas";
        } catch (RegraNegocioException e) {
            resultado.reject("matricula.invalida", e.getMessage());
            prepararFormulario(model, matricula, "Nova matrícula", "Registrar matrícula");
            return "matriculas/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        prepararFormulario(model, matriculaService.buscarPorId(id), "Editar matrícula", "Salvar alterações");
        return "matriculas/formulario";
    }

    @PostMapping("/{id}/atualizar")
    public String atualizar(@PathVariable String id,
                            @Valid @ModelAttribute("matricula") Matricula matricula,
                            BindingResult resultado,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        matricula.setId(id);
        if (resultado.hasErrors()) {
            prepararFormulario(model, matricula, "Editar matrícula", "Salvar alterações");
            return "matriculas/formulario";
        }
        try {
            matriculaService.atualizar(id, matricula);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Matrícula atualizada com sucesso.");
            return "redirect:/matriculas";
        } catch (RegraNegocioException e) {
            resultado.reject("matricula.invalida", e.getMessage());
            prepararFormulario(model, matricula, "Editar matrícula", "Salvar alterações");
            return "matriculas/formulario";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String id, RedirectAttributes redirectAttributes) {
        matriculaService.excluir(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Matrícula excluída com sucesso.");
        return "redirect:/matriculas";
    }

    private void prepararFormulario(Model model, Matricula matricula, String titulo, String textoBotao) {
        model.addAttribute("matricula", matricula);
        model.addAttribute("alunos", alunoService.listar());
        model.addAttribute("cursos", cursoService.listar());
        model.addAttribute("tituloFormulario", titulo);
        model.addAttribute("textoBotao", textoBotao);
    }
}
