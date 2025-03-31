package com.nnk.springboot.controllerTests;

import com.nnk.springboot.controllers.RuleNameController;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RuleNameController.class)
public class RuleNameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RuleNameRepository  ruleNameRepository;

    @Test
    @WithMockUser(roles = "USER")
    public void homeTest() throws Exception {

        when(ruleNameRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/ruleName/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/list"))
                .andExpect(content().string(containsString("Rule List")));

        verify(ruleNameRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void addRuleFormTest() throws Exception {

        this.mockMvc.perform(get("/ruleName/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(content().string(containsString("Add New Rule")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest() throws Exception {

        RuleName validRuleName = new RuleName(
                "name", "description","json","template","sqlStr","sqlPart");
        when(ruleNameRepository.save(any())).thenReturn(validRuleName);
        when(ruleNameRepository.findAll()).thenReturn(new ArrayList<>());


        this.mockMvc.perform(post("/ruleName/validate")
                        .flashAttr("ruleName", validRuleName)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"));

        verify(ruleNameRepository).save(any());
        verify(ruleNameRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest_withErrors() throws Exception {

        RuleName invalidRuleName = new RuleName(
                "", "description","json","template","sqlStr","sqlPart");
        RuleName invalidRuleDescription = new RuleName(
                "name", "","json","template","sqlStr","sqlPart");
        RuleName invalidRuleJson = new RuleName(
                "name", "description","","template","sqlStr","sqlPart");
        RuleName invalidRuleTemplate = new RuleName(
                "name", "description","json","","sqlStr","sqlPart");
        RuleName invalidRuleSqlStr = new RuleName(
                "name", "description","json","template","","sqlPart");
        RuleName invalidRuleSqlPart = new RuleName(
                "name", "description","json","template","sqlStr","");

        this.mockMvc.perform(post("/ruleName/validate")
                        .flashAttr("ruleName", invalidRuleName)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(content().string(containsString("Add New Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "name"));

        this.mockMvc.perform(post("/ruleName/validate")
                        .flashAttr("ruleName", invalidRuleDescription)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(content().string(containsString("Add New Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "description"));

        this.mockMvc.perform(post("/ruleName/validate")
                        .flashAttr("ruleName", invalidRuleJson)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(content().string(containsString("Add New Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "json"));

        this.mockMvc.perform(post("/ruleName/validate")
                        .flashAttr("ruleName", invalidRuleTemplate)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(content().string(containsString("Add New Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "template"));

        this.mockMvc.perform(post("/ruleName/validate")
                        .flashAttr("ruleName", invalidRuleSqlStr)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(content().string(containsString("Add New Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "sqlStr"));

        this.mockMvc.perform(post("/ruleName/validate")
                        .flashAttr("ruleName", invalidRuleSqlPart)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(content().string(containsString("Add New Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "sqlPart"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void showUpdateFormTest() throws Exception {

        RuleName validRuleName = new RuleName(
                "name", "description","json","template","sqlStr","sqlPart");
        when(ruleNameRepository.findById(anyInt())).thenReturn(Optional.of(validRuleName));

        this.mockMvc.perform(get("/ruleName/update/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(content().string(containsString("Update Rule")));

        verify(ruleNameRepository).findById(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void updateRuleTest() throws Exception {

        RuleName validRuleName = new RuleName(
                "name", "description","json","template","sqlStr","sqlPart");
        when(ruleNameRepository.save(any())).thenReturn(validRuleName);
        when(ruleNameRepository.findAll()).thenReturn(new ArrayList<>());


        this.mockMvc.perform(post("/ruleName/update/{id}", 1)
                        .flashAttr("ruleName", validRuleName)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"));

        verify(ruleNameRepository).save(any());
        verify(ruleNameRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void updateRuleTest_withErrors() throws Exception {

        RuleName invalidRuleName = new RuleName(
                "", "description","json","template","sqlStr","sqlPart");
        RuleName invalidRuleDescription = new RuleName(
                "name", "","json","template","sqlStr","sqlPart");
        RuleName invalidRuleJson = new RuleName(
                "name", "description","","template","sqlStr","sqlPart");
        RuleName invalidRuleTemplate = new RuleName(
                "name", "description","json","","sqlStr","sqlPart");
        RuleName invalidRuleSqlStr = new RuleName(
                "name", "description","json","template","","sqlPart");
        RuleName invalidRuleSqlPart = new RuleName(
                "name", "description","json","template","sqlStr","");

        this.mockMvc.perform(post("/ruleName/update/{id}", 1)
                        .flashAttr("ruleName", invalidRuleName)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(content().string(containsString("Update Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "name"));

        this.mockMvc.perform(post("/ruleName/update/{id}", 1)
                        .flashAttr("ruleName", invalidRuleDescription)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(content().string(containsString("Update Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "description"));

        this.mockMvc.perform(post("/ruleName/update/{id}", 1)
                        .flashAttr("ruleName", invalidRuleJson)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(content().string(containsString("Update Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "json"));

        this.mockMvc.perform(post("/ruleName/update/{id}", 1)
                        .flashAttr("ruleName", invalidRuleTemplate)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(content().string(containsString("Update Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "template"));

        this.mockMvc.perform(post("/ruleName/update/{id}", 1)
                        .flashAttr("ruleName", invalidRuleSqlStr)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(content().string(containsString("Update Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "sqlStr"));

        this.mockMvc.perform(post("/ruleName/update/{id}", 1)
                        .flashAttr("ruleName", invalidRuleSqlPart)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(content().string(containsString("Update Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", "sqlPart"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteRuleTest() throws Exception {

        RuleName validRuleName = new RuleName(
                "name", "description","json","template","sqlStr","sqlPart");
        when(ruleNameRepository.findById(anyInt())).thenReturn(Optional.of(validRuleName));
        doNothing().when(ruleNameRepository).delete(any());
        when(ruleNameRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/ruleName/delete/{id}", 1))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"));

        verify(ruleNameRepository).findById(1);
        verify(ruleNameRepository).delete(any());
        verify(ruleNameRepository).findAll();
    }
}
