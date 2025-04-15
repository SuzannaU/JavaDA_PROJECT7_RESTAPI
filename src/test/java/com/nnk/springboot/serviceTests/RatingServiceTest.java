package com.nnk.springboot.serviceTests;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.services.RatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class RatingServiceTest {

    @Autowired
    private RatingService ratingService;

    @MockitoBean
    private RatingRepository ratingRepository;

    @Test
    public void findAll_shouldReturnList() {
        when(ratingRepository.findAll()).thenReturn(new ArrayList<>());

        List<Rating> ratings = ratingService.findAll();

        assertNotNull(ratings);
        verify(ratingRepository).findAll();
    }

    @Test
    public void save_shouldReturnSavedRating() {
        Rating rating = new Rating("moodys", "sandP", "fitch", 1);
        when(ratingRepository.save(any(Rating.class))).thenAnswer(i -> i.getArgument(0));

        Rating savedRating = ratingService.save(rating);

        assertNotNull(savedRating);
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    public void findById_shouldReturnRating() {
        when(ratingRepository.findById(anyInt()))
                .thenReturn(Optional.of(new Rating("moodys", "sandP", "fitch", 1)));

        Rating rating = ratingService.findById(1);

        assertNotNull(rating);
        verify(ratingRepository).findById(anyInt());
    }

    @Test
    public void findById_withInvalidId_shouldThrowException() {
        when(ratingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> ratingService.findById(1));

        verify(ratingRepository).findById(anyInt());
    }

    @Test
    public void delete_shouldCallRepo() {
        Rating rating = new Rating("moodys", "sandP", "fitch", 1);
        doNothing().when(ratingRepository).delete(any(Rating.class));

        ratingService.delete(rating);

        verify(ratingRepository).delete(rating);
    }
}
