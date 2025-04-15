package com.nnk.springboot.controllerTests;

import com.nnk.springboot.config.SpringSecurityConfig;
import com.nnk.springboot.controllers.RuleController;
import com.nnk.springboot.domain.Rule;
import com.nnk.springboot.repositories.RuleRepository;
import com.nnk.springboot.services.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RuleController.class)
@Import({SpringSecurityConfig.class})
public class RuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private RuleRepository ruleRepository;

    private Rule validRule;

    @BeforeEach
    public void setup() {
        validRule = new Rule(
                "name", "description", "json", "template", "sqlStr", "sqlPart");
    }

    private static Stream<Arguments> invalidRuleProvider() {
        return Stream.of(
                Arguments.of("nullName", new Rule(
                        null, "description", "json", "template", "sqlStr", "sqlPart"), "name"),
                Arguments.of("emptyName", new Rule(
                        "", "description", "json", "template", "sqlStr", "sqlPart"), "name"),
                Arguments.of("nullDescription", new Rule(
                        "name", null, "json", "template", "sqlStr", "sqlPart"), "description"),
                Arguments.of("emptyDescription", new Rule(
                        "name", "", "json", "template", "sqlStr", "sqlPart"), "description"),
                Arguments.of("nullJson", new Rule(
                        "name", "description", null, "template", "sqlStr", "sqlPart"), "json"),
                Arguments.of("emptyJson", new Rule(
                        "name", "description", "", "template", "sqlStr", "sqlPart"), "json"),
                Arguments.of("nullTemplate", new Rule(
                        "name", "description", "json", null, "sqlStr", "sqlPart"), "template"),
                Arguments.of("emptyTemplate", new Rule(
                        "name", "description", "json", "", "sqlStr", "sqlPart"), "template"),
                Arguments.of("nullSqlStr", new Rule(
                        "name", "description", "json", "template", null, "sqlPart"), "sqlStr"),
                Arguments.of("emptySqlStr", new Rule(
                        "name", "description", "json", "template", "", "sqlPart"), "sqlStr"),
                Arguments.of("nullSqlPart", new Rule(
                        "name", "description", "json", "template", "sqlStr", null), "sqlPart"),
                Arguments.of("emptySqlPart", new Rule(
                        "name", "description", "json", "template", "sqlStr", ""), "sqlPart")
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getHome_shouldReturnList() throws Exception {

        when(ruleRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/rule/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rule/list"))
                .andExpect(content().string(containsString("Rule List")));

        verify(ruleRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAddRuleForm_shouldReturnForm() throws Exception {

        this.mockMvc.perform(get("/rule/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rule/add"))
                .andExpect(content().string(containsString("Add New Rule")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void postValidate_shouldSaveRule() throws Exception {

        when(ruleRepository.save(any())).thenReturn(validRule);
        when(ruleRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/rule/validate")
                        .flashAttr("rule", validRule)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rule/list"));

        verify(ruleRepository).save(any());
        verify(ruleRepository).findAll();
    }

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidRuleProvider")
    @WithMockUser(roles = "USER")
    public void postValidate_withInvalidRuleName_shouldShowError(String testedAttribute, Rule invalidRule, String error) throws Exception {

        this.mockMvc.perform(post("/rule/validate")
                        .flashAttr("rule", invalidRule)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rule/add"))
                .andExpect(content().string(containsString("Add New Rule")))
                .andExpect(model().attributeHasFieldErrors("rule", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getShowUpdateForm_shouldReturnForm() throws Exception {

        when(ruleRepository.findById(anyInt())).thenReturn(Optional.of(validRule));

        this.mockMvc.perform(get("/rule/update/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rule/update"))
                .andExpect(content().string(containsString("Update Rule")));

        verify(ruleRepository).findById(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void postUpdateRule_shouldSaveRule() throws Exception {

        when(ruleRepository.save(any())).thenReturn(validRule);
        when(ruleRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/rule/update/{id}", 1)
                        .flashAttr("rule", validRule)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rule/list"));

        verify(ruleRepository).save(any());
        verify(ruleRepository).findAll();
    }

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidRuleProvider")
    @WithMockUser(roles = "USER")
    public void postUpdateRule_withInvalidRule_shouldShowError(String testedAttribute, Rule invalidRule, String error) throws Exception {

        this.mockMvc.perform(post("/rule/update/{id}", 1)
                        .flashAttr("rule", invalidRule)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rule/update"))
                .andExpect(content().string(containsString("Update Rule")))
                .andExpect(model().attributeHasFieldErrors("rule", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getDeleteRule_shouldDeleteRule() throws Exception {

        when(ruleRepository.findById(anyInt())).thenReturn(Optional.of(validRule));
        doNothing().when(ruleRepository).delete(any());
        when(ruleRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/rule/delete/{id}", 1))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rule/list"));

        verify(ruleRepository).findById(1);
        verify(ruleRepository).delete(any());
        verify(ruleRepository).findAll();
    }
}
