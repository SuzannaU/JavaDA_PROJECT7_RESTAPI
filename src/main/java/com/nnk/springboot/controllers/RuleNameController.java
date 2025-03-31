package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RuleNameController {

    private final RuleNameRepository ruleNameRepository;

    public RuleNameController(RuleNameRepository ruleNameRepository) {
        this.ruleNameRepository = ruleNameRepository;
    }

    @RequestMapping("/ruleName/list")
    public String home(Model model) {
        model.addAttribute("ruleNames", ruleNameRepository.findAll());
        return "ruleName/list";
    }

    @GetMapping("/ruleName/add")
    public String addRuleForm(Model model) {
        RuleName ruleName = new RuleName();
        model.addAttribute("ruleName", ruleName);
        return "ruleName/add";
    }

    @PostMapping("/ruleName/validate")
    public String validate(
            @Valid @ModelAttribute("ruleName") RuleName ruleName,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "ruleName/add";
        }
        ruleNameRepository.save(ruleName);
        model.addAttribute("ruleNames", ruleNameRepository.findAll());
        return "redirect:/ruleName/list";
    }

    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        RuleName ruleName = ruleNameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid RuleName Id: " + id));
        model.addAttribute("ruleName", ruleName);
        return "ruleName/update";
    }

    @PostMapping("/ruleName/update/{id}")
    public String updateRuleName(@PathVariable("id") Integer id, @Valid RuleName ruleName,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "ruleName/update";
        }
        ruleNameRepository.save(ruleName);
        model.addAttribute("ruleNames", ruleNameRepository.findAll());
        return "redirect:/ruleName/list";
    }

    @GetMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id, Model model) {
        RuleName ruleName = ruleNameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid RuleName Id: " + id));
        ruleNameRepository.delete(ruleName);
        model.addAttribute("ruleNames", ruleNameRepository.findAll());
        return "redirect:/ruleName/list";
    }
}
