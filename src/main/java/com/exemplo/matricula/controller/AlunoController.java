package com.exemplo.matricula.controller;

import com.exemplo.matricula.model.Aluno;
import com.exemplo.matricula.service.AlunoService;
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
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("alunos", alunoService.listar());
        return "alunos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        prepararFormulario(model, new Aluno(), "Novo aluno", "Cadastrar aluno");
        return "alunos/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("aluno") Aluno aluno,
                         BindingResult resultado,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            prepararFormulario(model, aluno, "Novo aluno", "Cadastrar aluno");
            return "alunos/formulario";
        }
        try {
            alunoService.salvar(aluno);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Aluno cadastrado com sucesso.");
            return "redirect:/alunos";
        } catch (RegraNegocioException e) {
            resultado.rejectValue("cpf", "cpf.duplicado", e.getMessage());
            prepararFormulario(model, aluno, "Novo aluno", "Cadastrar aluno");
            return "alunos/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        prepararFormulario(model, alunoService.buscarPorId(id), "Editar aluno", "Salvar alterações");
        return "alunos/formulario";
    }

    @PostMapping("/{id}/atualizar")
    public String atualizar(@PathVariable String id,
                            @Valid @ModelAttribute("aluno") Aluno aluno,
                            BindingResult resultado,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        aluno.setId(id);
        if (resultado.hasErrors()) {
            prepararFormulario(model, aluno, "Editar aluno", "Salvar alterações");
            return "alunos/formulario";
        }
        try {
            alunoService.atualizar(id, aluno);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Dados do aluno atualizados.");
            return "redirect:/alunos";
        } catch (RegraNegocioException e) {
            resultado.rejectValue("cpf", "cpf.duplicado", e.getMessage());
            prepararFormulario(model, aluno, "Editar aluno", "Salvar alterações");
            return "alunos/formulario";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            alunoService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Aluno excluído com sucesso.");
        } catch (RegraNegocioException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/alunos";
    }

    private void prepararFormulario(Model model, Aluno aluno, String titulo, String textoBotao) {
        model.addAttribute("aluno", aluno);
        model.addAttribute("tituloFormulario", titulo);
        model.addAttribute("textoBotao", textoBotao);
    }
}
