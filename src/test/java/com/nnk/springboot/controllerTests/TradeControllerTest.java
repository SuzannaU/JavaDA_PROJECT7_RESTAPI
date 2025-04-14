package com.nnk.springboot.controllerTests;

import com.nnk.springboot.config.SpringSecurityConfig;
import com.nnk.springboot.controllers.TradeController;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
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

@WebMvcTest(TradeController.class)
@Import({SpringSecurityConfig.class})
public class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private TradeRepository tradeRepository;

    private Trade validTrade;

    @BeforeEach
    public void setup() {
        validTrade = new Trade("account", "type", 10d);
    }

    private static Stream<Arguments> invalidTradeProvider() {
        return Stream.of(
                Arguments.of("nullAccount", new Trade(null, "type", 10d), "account"),
                Arguments.of("emptyAccount", new Trade("", "type", 10d), "account"),
                Arguments.of("nullType", new Trade("account", null, 10d), "type"),
                Arguments.of("emptyType", new Trade("account", "", 10d), "type"),
                Arguments.of("invalidQuantity", new Trade("account", "type", 0.1d), "buyQuantity")
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getHome_shouldReturnList() throws Exception {

        when(tradeRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/trade/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/list"))
                .andExpect(content().string(containsString("Trade List")));

        verify(tradeRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAddTradeForm_shouldReturnForm() throws Exception {

        this.mockMvc.perform(get("/trade/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"))
                .andExpect(content().string(containsString("Add New Trade")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void postValidate_shouldSaveTrade() throws Exception {

        when(tradeRepository.save(any())).thenReturn(validTrade);
        when(tradeRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/trade/validate")
                        .flashAttr("trade", validTrade)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trade/list"));

        verify(tradeRepository).save(any());
        verify(tradeRepository).findAll();
    }

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidTradeProvider")
    @WithMockUser(roles = "USER")
    public void postValidate_withInvalidTrade_shouldShowError(String testedAttribute, Trade invalidTrade, String error) throws Exception {

        this.mockMvc.perform(post("/trade/validate")
                        .flashAttr("trade", invalidTrade)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"))
                .andExpect(content().string(containsString("Add New Trade")))
                .andExpect(model().attributeHasFieldErrors("trade", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getShowUpdateForm_shouldReturnForm() throws Exception {

        when(tradeRepository.findById(anyInt())).thenReturn(Optional.of(validTrade));

        this.mockMvc.perform(get("/trade/update/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/update"))
                .andExpect(content().string(containsString("Update Trade")));

        verify(tradeRepository).findById(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void postUpdateTrade_shouldSaveBidList() throws Exception {

        when(tradeRepository.save(any())).thenReturn(validTrade);
        when(tradeRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/trade/update/{id}", 1)
                        .flashAttr("trade", validTrade)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trade/list"));

        verify(tradeRepository).save(any());
        verify(tradeRepository).findAll();
    }

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidTradeProvider")
    @WithMockUser(roles = "USER")
    public void postUpdateTrade_withInvalidTrade_shouldShowError(String testedAttribute, Trade invalidTrade, String error) throws Exception {

        this.mockMvc.perform(post("/trade/update/{id}", 1)
                        .flashAttr("trade", invalidTrade)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/update"))
                .andExpect(content().string(containsString("Update Trade")))
                .andExpect(model().attributeHasFieldErrors("trade", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getDeleteTrade_shouldDeleteTrade() throws Exception {

        when(tradeRepository.findById(anyInt())).thenReturn(Optional.of(validTrade));
        doNothing().when(tradeRepository).delete(any());
        when(tradeRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/trade/delete/{id}", 1))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trade/list"));

        verify(tradeRepository).findById(1);
        verify(tradeRepository).delete(any());
        verify(tradeRepository).findAll();
    }
}
