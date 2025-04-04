package com.nnk.springboot.controllerTests;

import com.nnk.springboot.controllers.TradeController;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TradeController.class)
public class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private TradeRepository tradeRepository;

    @Test
    @WithMockUser(roles = "USER")
    public void homeTest() throws Exception {

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
    public void addTradeFormTest() throws Exception {

        this.mockMvc.perform(get("/trade/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"))
                .andExpect(content().string(containsString("Add New Trade")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest() throws Exception {

        Trade validTrade = new Trade("account", "type", 10d);
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

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest_withErrors() throws Exception {

        Trade invalidTradeAccount = new Trade("", "type", 10d);
        Trade invalidTradeType = new Trade("account", "", 10d);
        Trade invalidTradeQty = new Trade("account", "type", 0.1d);

        this.mockMvc.perform(post("/trade/validate")
                        .flashAttr("trade", invalidTradeAccount)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"))
                .andExpect(content().string(containsString("Add New Trade")))
                .andExpect(model().attributeHasFieldErrors("trade", "account"));

        this.mockMvc.perform(post("/trade/validate")
                        .flashAttr("trade", invalidTradeType)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"))
                .andExpect(content().string(containsString("Add New Trade")))
                .andExpect(model().attributeHasFieldErrors("trade", "type"));

        this.mockMvc.perform(post("/trade/validate")
                        .flashAttr("trade", invalidTradeQty)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/add"))
                .andExpect(content().string(containsString("Add New Trade")))
                .andExpect(model().attributeHasFieldErrors("trade", "buyQuantity"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void showUpdateFormTest() throws Exception {

        Trade validTrade = new Trade("account", "type", 10d);
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
    public void updateTradeTest() throws Exception {

        Trade validTrade = new Trade("account", "type", 10d);
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

    @Test
    @WithMockUser(roles = "USER")
    public void updateTradeTest_withErrors() throws Exception {

        Trade invalidTradeAccount = new Trade("", "type", 10d);
        Trade invalidTradeType = new Trade("account", "", 10d);
        Trade invalidTradeQty = new Trade("account", "type", 0.1d);

        this.mockMvc.perform(post("/trade/update/{id}", 1)
                        .flashAttr("trade", invalidTradeAccount)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/update"))
                .andExpect(content().string(containsString("Update Trade")))
                .andExpect(model().attributeHasFieldErrors("trade", "account"));

        this.mockMvc.perform(post("/trade/update/{id}", 1)
                        .flashAttr("trade", invalidTradeType)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/update"))
                .andExpect(content().string(containsString("Update Trade")))
                .andExpect(model().attributeHasFieldErrors("trade", "type"));

        this.mockMvc.perform(post("/trade/update/{id}", 1)
                        .flashAttr("trade", invalidTradeQty)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("trade/update"))
                .andExpect(content().string(containsString("Update Trade")))
                .andExpect(model().attributeHasFieldErrors("trade", "buyQuantity"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteTradeTest() throws Exception {

        Trade validTrade = new Trade("account", "type", 10d);
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
