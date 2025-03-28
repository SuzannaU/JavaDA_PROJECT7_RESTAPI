package com.nnk.springboot.controllerTests;

import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
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

@WebMvcTest(BidListController.class)
public class BidListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BidListRepository bidListRepository;

    @Test
    @WithMockUser(roles = "USER")
    public void homeTest() throws Exception {

        when(bidListRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/bidList/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"))
                .andExpect(content().string(containsString("Bid List")));

        verify(bidListRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void addBidFormTest() throws Exception {

        this.mockMvc.perform(get("/bidList/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(content().string(containsString("Add New Bid")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest() throws Exception {

        BidList validBidList = new BidList("account", "type", 10d);
        when(bidListRepository.save(any())).thenReturn(validBidList);
        when(bidListRepository.findAll()).thenReturn(new ArrayList<>());


        this.mockMvc.perform(post("/bidList/validate")
                        .flashAttr("bidList", validBidList)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"));

        verify(bidListRepository).save(any());
        verify(bidListRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest_withErrors() throws Exception {

        BidList invalidBidListAccount = new BidList("", "type", 10d);
        BidList invalidBidListType = new BidList("account", "", 10d);
        BidList invalidBidListQty = new BidList("account", "type", 0.1d);

        this.mockMvc.perform(post("/bidList/validate")
                        .flashAttr("bidList", invalidBidListAccount)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(content().string(containsString("Add New Bid")))
                .andExpect(model().attributeHasFieldErrors("bidList", "account"));

        this.mockMvc.perform(post("/bidList/validate")
                        .flashAttr("bidList", invalidBidListType)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(content().string(containsString("Add New Bid")))
                .andExpect(model().attributeHasFieldErrors("bidList", "type"));

        this.mockMvc.perform(post("/bidList/validate")
                        .flashAttr("bidList", invalidBidListQty)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(content().string(containsString("Add New Bid")))
                .andExpect(model().attributeHasFieldErrors("bidList", "bidQuantity"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void showUpdateFormTest() throws Exception {

        BidList validBidList = new BidList("account", "type", 10d);
        when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(validBidList));

        this.mockMvc.perform(get("/bidList/update/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(content().string(containsString("Update Bid")));

        verify(bidListRepository).findById(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void updateBidTest() throws Exception {

        BidList validBidList = new BidList("account", "type", 10d);
        when(bidListRepository.save(any())).thenReturn(validBidList);
        when(bidListRepository.findAll()).thenReturn(new ArrayList<>());


        this.mockMvc.perform(post("/bidList/update/{id}", 1)
                        .flashAttr("bidList", validBidList)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"));

        verify(bidListRepository).save(any());
        verify(bidListRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void updateBidTest_withErrors() throws Exception {

        BidList invalidBidListAccount = new BidList("", "type", 10d);
        BidList invalidBidListType = new BidList("account", "", 10d);
        BidList invalidBidListQty = new BidList("account", "type", 0.1d);

        this.mockMvc.perform(post("/bidList/update/{id}", 1)
                        .flashAttr("bidList", invalidBidListAccount)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(content().string(containsString("Update Bid")))
                .andExpect(model().attributeHasFieldErrors("bidList", "account"));

        this.mockMvc.perform(post("/bidList/update/{id}", 1)
                        .flashAttr("bidList", invalidBidListType)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(content().string(containsString("Update Bid")))
                .andExpect(model().attributeHasFieldErrors("bidList", "type"));

        this.mockMvc.perform(post("/bidList/update/{id}", 1)
                        .flashAttr("bidList", invalidBidListQty)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(content().string(containsString("Update Bid")))
                .andExpect(model().attributeHasFieldErrors("bidList", "bidQuantity"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteBidTest() throws Exception {

        BidList validBidList = new BidList("account", "type", 10d);
        when(bidListRepository.findById(anyInt())).thenReturn(Optional.of(validBidList));
        doNothing().when(bidListRepository).delete(any());
        when(bidListRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/bidList/delete/{id}", 1))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"));

        verify(bidListRepository).findById(1);
        verify(bidListRepository).delete(any());
        verify(bidListRepository).findAll();
    }
}
