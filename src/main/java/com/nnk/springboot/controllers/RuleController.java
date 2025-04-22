package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rule;
import com.nnk.springboot.services.RuleService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing rules.
 * Handles requests for listing, adding, updating, and deleting rules.
 */
@Controller
@RequestMapping("rule")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    /**
     * Displays the list of all rules.
     *
     * @param model to add to the template
     * @return template for listing all rules
     */
    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("rules", ruleService.findAll());
        return "rule/list";
    }

    /**
     * Fetches the add template for a new rule.
     *
     * @param model to add to the template
     * @return template for adding a new rule
     */
    @GetMapping("add")
    public String addRuleForm(Model model) {
        Rule rule = new Rule();
        model.addAttribute("rule", rule);
        return "rule/add";
    }

    /**
     * Validates and saves a new rule.
     *
     * @param rule   the rule to be added
     * @param result the result of the validation
     * @param model  to add to the template
     * @return redirect to the list of rules if successful, otherwise show the add form again
     */
    @PostMapping("validate")
    public String validate(
            @Valid @ModelAttribute("rule") Rule rule,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "rule/add";
        }
        ruleService.save(rule);
        model.addAttribute("rules", ruleService.findAll());
        return "redirect:/rule/list";
    }

    /**
     * Fetches the update template for a specific rule.
     *
     * @param id    the ID of the rule to be updated
     * @param model to add to the template
     * @return template for updating the rule
     */
    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Rule rule = ruleService.findById(id);
        model.addAttribute("rule", rule);
        return "rule/update";
    }

    /**
     * Validates and updates an existing rule.
     *
     * @param id     the ID of the rule to be updated
     * @param rule   the updated rule
     * @param result the result of the validation
     * @param model  to add to the template
     * @return redirect to the list of rules if successful, otherwise show the update form again
     */
    @PostMapping("update/{id}")
    public String updateRule(@PathVariable("id") Integer id, @Valid Rule rule,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "rule/update";
        }
        ruleService.save(rule);
        model.addAttribute("rules", ruleService.findAll());
        return "redirect:/rule/list";
    }

    /**
     * Deletes a specific rule.
     *
     * @param id    the ID of the rule to be deleted
     * @param model to add to the template
     * @return redirect to the list of rules
     */
    @GetMapping("delete/{id}")
    public String deleteRule(@PathVariable("id") Integer id, Model model) {
        Rule rule = ruleService.findById(id);
        ruleService.delete(rule);
        model.addAttribute("rules", ruleService.findAll());
        return "redirect:/rule/list";
    }
}
