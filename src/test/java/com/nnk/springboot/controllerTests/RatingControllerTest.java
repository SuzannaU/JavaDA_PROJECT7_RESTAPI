package com.nnk.springboot.controllerTests;

import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
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

@WebMvcTest(RatingController.class)
public class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingRepository ratingRepository;

    @Test
    @WithMockUser(roles = "USER")
    public void homeTest() throws Exception {

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
    public void addBidFormTest() throws Exception {

        this.mockMvc.perform(get("/rating/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(content().string(containsString("Add New Rating")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest() throws Exception {

        Rating validRating = new Rating("Moodys", "SandP", "Fitch", 10);
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

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest_withErrors() throws Exception {

        Rating invalidRatingMoodys = new Rating("", "SandP", "Fitch", 10);
        Rating invalidRatingSandP = new Rating("Moodys", "", "Fitch", 10);
        Rating invalidRatingFitch = new Rating("Moodys", "SandP", "", 10);
        Rating invalidRatingOrder = new Rating("Moodys", "SandP", "Fitch", 0);

        this.mockMvc.perform(post("/rating/validate")
                        .flashAttr("rating", invalidRatingMoodys)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(content().string(containsString("Add New Rating")))
                .andExpect(model().attributeHasFieldErrors("rating", "moodysRating"));

        this.mockMvc.perform(post("/rating/validate")
                        .flashAttr("rating", invalidRatingSandP)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(content().string(containsString("Add New Rating")))
                .andExpect(model().attributeHasFieldErrors("rating", "sandPRating"));

        this.mockMvc.perform(post("/rating/validate")
                        .flashAttr("rating", invalidRatingFitch)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(content().string(containsString("Add New Rating")))
                .andExpect(model().attributeHasFieldErrors("rating", "fitchRating"));

        this.mockMvc.perform(post("/rating/validate")
                        .flashAttr("rating", invalidRatingOrder)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(content().string(containsString("Add New Rating")))
                .andExpect(model().attributeHasFieldErrors("rating", "orderNumber"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void showUpdateFormTest() throws Exception {

        Rating validRating = new Rating("Moodys", "SandP", "Fitch", 10);
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
    public void updateBidTest() throws Exception {

        Rating validRating = new Rating("Moodys", "SandP", "Fitch", 10);
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

    @Test
    @WithMockUser(roles = "USER")
    public void updateBidTest_withErrors() throws Exception {

        Rating invalidRatingMoodys = new Rating("", "SandP", "Fitch", 10);
        Rating invalidRatingSandP = new Rating("Moodys", "", "Fitch", 10);
        Rating invalidRatingFitch = new Rating("Moodys", "SandP", "", 10);
        Rating invalidRatingOrder = new Rating("Moodys", "SandP", "Fitch", 0);

        this.mockMvc.perform(post("/rating/update/{id}", 1)
                        .flashAttr("rating", invalidRatingMoodys)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(content().string(containsString("Update Rating")))
                .andExpect(model().attributeHasFieldErrors("rating", "moodysRating"));

        this.mockMvc.perform(post("/rating/update/{id}", 1)
                        .flashAttr("rating", invalidRatingSandP)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(content().string(containsString("Update Rating")))
                .andExpect(model().attributeHasFieldErrors("rating", "sandPRating"));

        this.mockMvc.perform(post("/rating/update/{id}", 1)
                        .flashAttr("rating", invalidRatingFitch)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(content().string(containsString("Update Rating")))
                .andExpect(model().attributeHasFieldErrors("rating", "fitchRating"));

        this.mockMvc.perform(post("/rating/update/{id}", 1)
                        .flashAttr("rating", invalidRatingOrder)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(content().string(containsString("Update Rating")))
                .andExpect(model().attributeHasFieldErrors("rating", "orderNumber"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteBidTest() throws Exception {

        Rating validRating = new Rating("Moodys", "SandP", "Fitch", 10);
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
