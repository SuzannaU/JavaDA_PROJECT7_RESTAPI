package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rule;
import com.nnk.springboot.repositories.RuleRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("rule")
public class RuleController {

    private final RuleRepository ruleRepository;

    public RuleController(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("rules", ruleRepository.findAll());
        return "rule/list";
    }

    @GetMapping("add")
    public String addRuleForm(Model model) {
        Rule rule = new Rule();
        model.addAttribute("rule", rule);
        return "rule/add";
    }

    @PostMapping("validate")
    public String validate(
            @Valid @ModelAttribute("rule") Rule rule,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "rule/add";
        }
        ruleRepository.save(rule);
        model.addAttribute("rules", ruleRepository.findAll());
        return "redirect:/rule/list";
    }

    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Rule Id: " + id));
        model.addAttribute("rule", rule);
        return "rule/update";
    }

    @PostMapping("update/{id}")
    public String updateRule(@PathVariable("id") Integer id, @Valid Rule rule,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "rule/update";
        }
        ruleRepository.save(rule);
        model.addAttribute("rules", ruleRepository.findAll());
        return "redirect:/rule/list";
    }

    @GetMapping("delete/{id}")
    public String deleteRule(@PathVariable("id") Integer id, Model model) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Rule Id: " + id));
        ruleRepository.delete(rule);
        model.addAttribute("rules", ruleRepository.findAll());
        return "redirect:/rule/list";
    }
}
