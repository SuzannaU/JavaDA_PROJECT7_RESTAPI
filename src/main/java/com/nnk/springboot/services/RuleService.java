package com.nnk.springboot.services;

import com.nnk.springboot.domain.Rule;
import com.nnk.springboot.repositories.RuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public List<Rule> findAll() {
        return ruleRepository.findAll();
    }

    public Rule save(Rule rule) {
        return ruleRepository.save(rule);
    }

    public Rule findById(Integer id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Rule Id: " + id));
    }

    public void delete(Rule rule) {
        ruleRepository.delete(rule);
    }
}
