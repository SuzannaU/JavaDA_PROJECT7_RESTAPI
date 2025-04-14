package com.nnk.springboot.controllerTests;

import com.nnk.springboot.config.SpringSecurityConfig;
import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
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

@WebMvcTest(BidListController.class)
@Import({SpringSecurityConfig.class})
public class BidListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private BidListRepository bidListRepository;

    private BidList validBidList;

    @BeforeEach
    public void setup() {
        validBidList = new BidList("account", "type", 10d);
    }

    private static Stream<Arguments> invalidBidListProvider() {
        return Stream.of(
                Arguments.of("nullAccount", new BidList(null, "type", 10d), "account"),
                Arguments.of("emptyAccount", new BidList("", "type", 10d), "account"),
                Arguments.of("nullType", new BidList("account", null, 10d), "type"),
                Arguments.of("emptyType", new BidList("account", "", 10d), "type"),
                Arguments.of("invalidBidQuantity", new BidList("account", "type", 0.1d), "bidQuantity")
        );
    }

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

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidBidListProvider")
    @WithMockUser(roles = "USER")
    public void validateTest_withErrors(String testedAttribute, BidList invalidBidList, String error) throws Exception {

        this.mockMvc.perform(post("/bidList/validate")
                        .flashAttr("bidList", invalidBidList)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(content().string(containsString("Add New Bid")))
                .andExpect(model().attributeHasFieldErrors("bidList", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void showUpdateFormTest() throws Exception {

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

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidBidListProvider")
    @WithMockUser(roles = "USER")
    public void updateBidTest_withErrors(String testedAttribute, BidList invalidBidList, String error) throws Exception {

        this.mockMvc.perform(post("/bidList/update/{id}", 1)
                        .flashAttr("bidList", invalidBidList)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(content().string(containsString("Update Bid")))
                .andExpect(model().attributeHasFieldErrors("bidList", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteBidTest() throws Exception {

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
