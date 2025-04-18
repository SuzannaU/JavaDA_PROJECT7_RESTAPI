package com.nnk.springboot;

import com.nnk.springboot.domain.*;
import com.nnk.springboot.services.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityTest {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BidService bidService;

    @MockitoBean
    private CurvePointService curvePointService;

    @MockitoBean
    private RatingService ratingService;

    @MockitoBean
    private RuleService ruleService;

    @MockitoBean
    private TradeService tradeService;

    @Test
    public void encoderTest() {
        String password = "password";
        String encodedPassword = passwordEncoder.encode(password);
        assertTrue(passwordEncoder.matches(password, encodedPassword));
    }

    @Test
    public void userLogin_successTest() throws Exception {
        mockMvc.perform(formLogin("/login").user("user").password("user")).andExpect(authenticated());
    }

    @Test
    public void userLogin_failTest() throws Exception {
        mockMvc.perform(formLogin("/login").user("user").password("wrongpassword")).andExpect(unauthenticated());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/bid/add", "/bid/list", "/bid/update",
            "/curvePoint/add", "/curvePoint/list", "/curvePoint/update",
            "/rating/add", "/rating/list", "/rating/update",
            "/rule/add", "/rule/list", "/rule/update",
            "/trade/add", "/trade/list", "/trade/update",
            "/user/add", "/user/list", "/user/update" })
    @WithAnonymousUser
    public void anonymousUser_isRedirectedToLoginTest(String url) throws Exception {
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/bid/add", "/bid/list", "/bid/update/1",
            "/curvePoint/add", "/curvePoint/list", "/curvePoint/update/1",
            "/rating/add", "/rating/list", "/rating/update/1",
            "/rule/add", "/rule/list", "/rule/update/1",
            "/trade/add", "/trade/list", "/trade/update/1"})
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void userOrAdmin_isOkTest(String url) throws Exception {

        when(bidService.findById(1)).thenReturn((new Bid()));
        when(curvePointService.findById(1)).thenReturn((new CurvePoint()));
        when(ratingService.findById(1)).thenReturn((new Rating()));
        when(ruleService.findById(1)).thenReturn((new Rule()));
        when(tradeService.findById(1)).thenReturn((new Trade()));

        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/user/add", "/user/list", "/user/update/1" })
    @WithMockUser(roles = "USER")
    public void user_isForbiddenTest(String url) throws Exception {
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
