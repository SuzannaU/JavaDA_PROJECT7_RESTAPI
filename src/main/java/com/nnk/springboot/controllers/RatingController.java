package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("rating")
public class RatingController {

    private final RatingRepository ratingRepository;

    public RatingController(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("ratings", ratingRepository.findAll());
        return "rating/list";
    }

    @GetMapping("add")
    public String addRatingForm(Model model) {
        Rating rating = new Rating();
        model.addAttribute("rating", rating);
        return "rating/add";
    }

    @PostMapping("validate")
    public String validate(
            @Valid @ModelAttribute("rating") Rating rating,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "rating/add";
        }
        ratingRepository.save(rating);
        model.addAttribute("ratings", ratingRepository.findAll());
        return "redirect:/rating/list";
    }


    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rating Id: " + id));
        model.addAttribute("rating", rating);
        return "rating/update";
    }

    @PostMapping("update/{id}")
    public String updateRating(@PathVariable("id") Integer id, @Valid Rating rating,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "rating/update";
        }
        ratingRepository.save(rating);
        model.addAttribute("ratings", ratingRepository.findAll());
        return "redirect:/rating/list";
    }

    @GetMapping("delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, Model model) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rating Id: " + id));
        ratingRepository.delete(rating);
        model.addAttribute("ratings", ratingRepository.findAll());
        return "redirect:/rating/list";
    }
}
