package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.RatingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing ratings.
 * Handles requests for listing, adding, updating, and deleting ratings.
 */
@Controller
@RequestMapping("rating")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Displays the list of all ratings.
     *
     * @param model to add to the template
     * @return template for listing all ratings
     */
    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("ratings", ratingService.findAll());
        return "rating/list";
    }

    /**
     * Fetches the add template for a new rating.
     *
     * @param model to add to the template
     * @return template for adding a new rating
     */
    @GetMapping("add")
    public String addRatingForm(Model model) {
        Rating rating = new Rating();
        model.addAttribute("rating", rating);
        return "rating/add";
    }

    /**
     * Validates and saves a new rating.
     *
     * @param rating    the rating to be added
     * @param result the result of the validation
     * @param model  to add to the template
     * @return redirect to the list of ratings if successful, otherwise show the add form again
     */
    @PostMapping("validate")
    public String validate(
            @Valid @ModelAttribute("rating") Rating rating,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "rating/add";
        }
        ratingService.save(rating);
        model.addAttribute("ratings", ratingService.findAll());
        return "redirect:/rating/list";
    }

    /**
     * Fetches the update template for a specific rating.
     *
     * @param id    the ID of the rating to be updated
     * @param model to add to the template
     * @return template for updating the rating
     */
    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Rating rating = ratingService.findById(id);
        model.addAttribute("rating", rating);
        return "rating/update";
    }

    /**
     * Validates and updates an existing rating.
     *
     * @param id     the ID of the rating to be updated
     * @param rating    the updated rating
     * @param result the result of the validation
     * @param model  to add to the template
     * @return redirect to the list of ratings if successful, otherwise show the update form again
     */
    @PostMapping("update/{id}")
    public String updateRating(@PathVariable("id") Integer id, @Valid Rating rating,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "rating/update";
        }
        ratingService.save(rating);
        model.addAttribute("ratings", ratingService.findAll());
        return "redirect:/rating/list";
    }

    /**
     * Deletes a specific rating.
     *
     * @param id    the ID of the rating to be deleted
     * @param model to add to the template
     * @return redirect to the list of ratings
     */
    @GetMapping("delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, Model model) {
        Rating rating = ratingService.findById(id);
        ratingService.delete(rating);
        model.addAttribute("ratings", ratingService.findAll());
        return "redirect:/rating/list";
    }
}
