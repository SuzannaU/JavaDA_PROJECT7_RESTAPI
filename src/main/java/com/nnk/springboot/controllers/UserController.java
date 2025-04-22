package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for managing users.
 * Handles requests for listing, adding, updating, and deleting users.
 */
@Controller
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the list of all users.
     *
     * @param model to add to the template
     * @return template for listing all users
     */
    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    /**
     * Fetches the add template for a new user.
     *
     * @param model to add to the template
     * @return template for adding a new user
     */
    @GetMapping("add")
    public String addUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "user/add";
    }

    /**
     * Validates and saves a new user.
     *
     * @param user   the user to be added
     * @param result the result of the validation
     * @param model  to add to the template
     * @return redirect to the list of users if successful, otherwise show the add form again
     */
    @PostMapping("validate")
    public String validate(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/add";
        }
        userService.save(user);
        model.addAttribute("users", userService.findAll());
        return "redirect:/user/list";
    }

    /**
     * Fetches the update template for a specific user.
     *
     * @param id    the ID of the user to be updated
     * @param model to add to the template
     * @return template for updating the user
     */
    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        User user = userService.findById(id);
        user.setPassword("");
        model.addAttribute("user", user);
        return "user/update";
    }

    /**
     * Validates and updates an existing user.
     *
     * @param id     the ID of the user to be updated
     * @param user   the updated user
     * @param result the result of the validation
     * @param model  to add to the template
     * @return redirect to the list of users if successful, otherwise show the update form again
     */
    @PostMapping("update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid User user,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/update";
        }
        userService.save(user);
        model.addAttribute("users", userService.findAll());
        return "redirect:/user/list";
    }

    /**
     * Deletes a specific user.
     *
     * @param id    the ID of the user to be deleted
     * @param model to add to the template
     * @return redirect to the list of users
     */
    @GetMapping("delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, Model model) {
        User user = userService.findById(id);
        userService.delete(user);
        model.addAttribute("users", userService.findAll());
        return "redirect:/user/list";
    }
}
