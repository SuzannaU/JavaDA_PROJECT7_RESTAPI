package com.nnk.springboot.serviceTests;

import com.nnk.springboot.domain.Rule;
import com.nnk.springboot.repositories.RuleRepository;
import com.nnk.springboot.services.RuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class RuleServiceTest {

    @Autowired
    private RuleService ruleService;

    @MockitoBean
    private RuleRepository ruleRepository;

    @Test
    public void findAll_shouldReturnList() {
        when(ruleRepository.findAll()).thenReturn(new ArrayList<>());

        List<Rule> rules = ruleService.findAll();

        assertNotNull(rules);
        verify(ruleRepository).findAll();
    }

    @Test
    public void save_shouldReturnSavedRule() {
        Rule rule = new Rule("name", "description", "json", "template", "sqlStr", "sqlPart");
        when(ruleRepository.save(any(Rule.class))).thenAnswer(i -> i.getArgument(0));

        Rule savedRule = ruleService.save(rule);

        assertNotNull(savedRule);
        verify(ruleRepository).save(any(Rule.class));
    }

    @Test
    public void findById_shouldReturnRule() {
        when(ruleRepository.findById(anyInt()))
                .thenReturn(Optional.of(new Rule("name", "description", "json", "template", "sqlStr", "sqlPart")));

        Rule rule = ruleService.findById(1);

        assertNotNull(rule);
        verify(ruleRepository).findById(anyInt());
    }

    @Test
    public void findById_withInvalidId_shouldThrowException() {
        when(ruleRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> ruleService.findById(1));

        verify(ruleRepository).findById(anyInt());
    }

    @Test
    public void delete_shouldCallRepo() {
        Rule rule = new Rule("name", "description", "json", "template", "sqlStr", "sqlPart");
        doNothing().when(ruleRepository).delete(any(Rule.class));

        ruleService.delete(rule);

        verify(ruleRepository).delete(rule);
    }
}
