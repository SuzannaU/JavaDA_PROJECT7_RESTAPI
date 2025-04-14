package com.nnk.springboot.controllerTests;

import com.nnk.springboot.config.SpringSecurityConfig;
import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
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

@WebMvcTest(RatingController.class)
@Import({SpringSecurityConfig.class})
public class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private RatingRepository ratingRepository;

    private Rating validRating;

    @BeforeEach
    public void setup() {
        validRating = new Rating("Moodys", "SandP", "Fitch", 10);
    }

    private static Stream<Arguments> invalidRatingProvider() {
        return Stream.of(
                Arguments.of("nullMoodys", new Rating(null, "SandP", "Fitch", 10), "moodysRating"),
                Arguments.of("emptyMoodys", new Rating("", "SandP", "Fitch", 10), "moodysRating"),
                Arguments.of("nullSandP", new Rating("Moodys", null, "Fitch", 10), "sandPRating"),
                Arguments.of("emptySandP", new Rating("Moodys", "", "Fitch", 10), "sandPRating"),
                Arguments.of("nullFitch", new Rating("Moodys", "SandP", null, 10), "fitchRating"),
                Arguments.of("emptyFitch", new Rating("Moodys", "SandP", "", 10), "fitchRating"),
                Arguments.of("invalidOrder", new Rating("Moodys", "SandP", "Fitch", 0), "orderNumber")
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getHome_shouldReturnList() throws Exception {

        when(ratingRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/rating/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(content().string(containsString("Rating List")));

        verify(ratingRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getAddRatingForm_shouldReturnForm() throws Exception {

        this.mockMvc.perform(get("/rating/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(content().string(containsString("Add New Rating")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void postValidate_shouldSaveRating() throws Exception {

        when(ratingRepository.save(any())).thenReturn(validRating);
        when(ratingRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/rating/validate")
                        .flashAttr("rating", validRating)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list"));

        verify(ratingRepository).save(any());
        verify(ratingRepository).findAll();
    }

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidRatingProvider")
    @WithMockUser(roles = "USER")
    public void postValidate_withInvalidRating_shouldShowError(String testedAttribute, Rating invalidRating, String error) throws Exception {

        this.mockMvc.perform(post("/rating/validate")
                        .flashAttr("rating", invalidRating)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(content().string(containsString("Add New Rating")))
                .andExpect(model().attributeHasFieldErrors("rating", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getShowUpdateForm_shouldReturnForm() throws Exception {

        when(ratingRepository.findById(anyInt())).thenReturn(Optional.of(validRating));

        this.mockMvc.perform(get("/rating/update/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(content().string(containsString("Update Rating")));

        verify(ratingRepository).findById(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void postUpdateRating_shouldSaveRating() throws Exception {

        when(ratingRepository.save(any())).thenReturn(validRating);
        when(ratingRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/rating/update/{id}", 1)
                        .flashAttr("rating", validRating)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list"));

        verify(ratingRepository).save(any());
        verify(ratingRepository).findAll();
    }

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidRatingProvider")
    @WithMockUser(roles = "USER")
    public void postUpdateRating_withInvalidRating_shouldShowError(String testedAttribute, Rating invalidRating, String error) throws Exception {

        this.mockMvc.perform(post("/rating/update/{id}", 1)
                        .flashAttr("rating", invalidRating)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(content().string(containsString("Update Rating")))
                .andExpect(model().attributeHasFieldErrors("rating", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getDeleteRating_shouldDeleteRating() throws Exception {

        when(ratingRepository.findById(anyInt())).thenReturn(Optional.of(validRating));
        doNothing().when(ratingRepository).delete(any());
        when(ratingRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/rating/delete/{id}", 1))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list"));

        verify(ratingRepository).findById(1);
        verify(ratingRepository).delete(any());
        verify(ratingRepository).findAll();
    }
}
