package com.nnk.springboot.controllerTests;

import com.nnk.springboot.config.SpringSecurityConfig;
import com.nnk.springboot.controllers.RuleNameController;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
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

@WebMvcTest(RuleNameController.class)
@Import({SpringSecurityConfig.class})
public class RuleNameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private RuleNameRepository ruleNameRepository;

    private RuleName validRuleName;

    @BeforeEach
    public void setup() {
        validRuleName = new RuleName(
                "name", "description", "json", "template", "sqlStr", "sqlPart");
    }

    private static Stream<Arguments> invalidRuleNameProvider() {
        return Stream.of(
                Arguments.of("nullName", new RuleName(
                        null, "description", "json", "template", "sqlStr", "sqlPart"), "name"),
                Arguments.of("emptyName", new RuleName(
                        "", "description", "json", "template", "sqlStr", "sqlPart"), "name"),
                Arguments.of("nullDescription", new RuleName(
                        "name", null, "json", "template", "sqlStr", "sqlPart"), "description"),
                Arguments.of("emptyDescription", new RuleName(
                        "name", "", "json", "template", "sqlStr", "sqlPart"), "description"),
                Arguments.of("nullJson", new RuleName(
                        "name", "description", null, "template", "sqlStr", "sqlPart"), "json"),
                Arguments.of("emptyJson", new RuleName(
                        "name", "description", "", "template", "sqlStr", "sqlPart"), "json"),
                Arguments.of("nullTemplate", new RuleName(
                        "name", "description", "json", null, "sqlStr", "sqlPart"), "template"),
                Arguments.of("emptyTemplate", new RuleName(
                        "name", "description", "json", "", "sqlStr", "sqlPart"), "template"),
                Arguments.of("nullSqlStr", new RuleName(
                        "name", "description", "json", "template", null, "sqlPart"), "sqlStr"),
                Arguments.of("emptySqlStr", new RuleName(
                        "name", "description", "json", "template", "", "sqlPart"), "sqlStr"),
                Arguments.of("nullSqlPart", new RuleName(
                        "name", "description", "json", "template", "sqlStr", null), "sqlPart"),
                Arguments.of("emptySqlPart", new RuleName(
                        "name", "description", "json", "template", "sqlStr", ""), "sqlPart")
        );
    }

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

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidRuleNameProvider")
    @WithMockUser(roles = "USER")
    public void validateTest_withErrors(String testedAttribute, RuleName invalidRuleName, String error) throws Exception {

        this.mockMvc.perform(post("/ruleName/validate")
                        .flashAttr("ruleName", invalidRuleName)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/add"))
                .andExpect(content().string(containsString("Add New Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void showUpdateFormTest() throws Exception {

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

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidRuleNameProvider")
    @WithMockUser(roles = "USER")
    public void updateRuleTest_withErrors(String testedAttribute, RuleName invalidRuleName, String error) throws Exception {

        this.mockMvc.perform(post("/ruleName/update/{id}", 1)
                        .flashAttr("ruleName", invalidRuleName)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("ruleName/update"))
                .andExpect(content().string(containsString("Update Rule")))
                .andExpect(model().attributeHasFieldErrors("ruleName", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteRuleTest() throws Exception {

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
