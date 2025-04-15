package com.nnk.springboot.controllerTests;

import com.nnk.springboot.config.SpringSecurityConfig;
import com.nnk.springboot.controllers.BidController;
import com.nnk.springboot.domain.Bid;
import com.nnk.springboot.services.BidService;
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

@WebMvcTest(BidController.class)
@Import({SpringSecurityConfig.class})
public class BidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private BidService bidService;

    private Bid validBid;

    @BeforeEach
    public void setup() {
        validBid = new Bid("account", "type", 10d);
    }

    private static Stream<Arguments> invalidBidProvider() {
        return Stream.of(
                Arguments.of("nullAccount", new Bid(null, "type", 10d), "account"),
                Arguments.of("emptyAccount", new Bid("", "type", 10d), "account"),
                Arguments.of("nullType", new Bid("account", null, 10d), "type"),
                Arguments.of("emptyType", new Bid("account", "", 10d), "type"),
                Arguments.of("invalidBidQuantity", new Bid("account", "type", 0.1d), "bidQuantity")
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getHome_shouldReturnList() throws Exception {

        when(bidService.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/bid/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bid/list"))
                .andExpect(content().string(containsString("Bid List")));

        verify(bidService).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAddBidForm_shouldReturnForm() throws Exception {

        this.mockMvc.perform(get("/bid/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bid/add"))
                .andExpect(content().string(containsString("Add New Bid")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void postValidate_shouldSaveBid() throws Exception {

        when(bidService.save(any())).thenReturn(validBid);
        when(bidService.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/bid/validate")
                        .flashAttr("bid", validBid)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bid/list"));

        verify(bidService).save(any());
        verify(bidService).findAll();
    }

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidBidProvider")
    @WithMockUser(roles = "USER")
    public void postValidate_withInvalidBid_shouldShowError(String testedAttribute, Bid invalidBid, String error) throws Exception {

        this.mockMvc.perform(post("/bid/validate")
                        .flashAttr("bid", invalidBid)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bid/add"))
                .andExpect(content().string(containsString("Add New Bid")))
                .andExpect(model().attributeHasFieldErrors("bid", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getShowUpdateForm_shouldReturnForm() throws Exception {

        when(bidService.findById(anyInt())).thenReturn(validBid);

        this.mockMvc.perform(get("/bid/update/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bid/update"))
                .andExpect(content().string(containsString("Update Bid")));

        verify(bidService).findById(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void postUpdateBid_shouldSaveBid() throws Exception {

        when(bidService.save(any())).thenReturn(validBid);
        when(bidService.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/bid/update/{id}", 1)
                        .flashAttr("bid", validBid)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bid/list"));

        verify(bidService).save(any());
        verify(bidService).findAll();
    }

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidBidProvider")
    @WithMockUser(roles = "USER")
    public void postUpdateBid_withInvalidBid_shouldShowError(String testedAttribute, Bid invalidBid, String error) throws Exception {

        this.mockMvc.perform(post("/bid/update/{id}", 1)
                        .flashAttr("bid", invalidBid)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("bid/update"))
                .andExpect(content().string(containsString("Update Bid")))
                .andExpect(model().attributeHasFieldErrors("bid", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getDeleteBid_shouldDeleteBid() throws Exception {

        when(bidService.findById(anyInt())).thenReturn(validBid);
        doNothing().when(bidService).delete(any());
        when(bidService.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/bid/delete/{id}", 1))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bid/list"));

        verify(bidService).findById(1);
        verify(bidService).delete(any());
        verify(bidService).findAll();
    }
}
