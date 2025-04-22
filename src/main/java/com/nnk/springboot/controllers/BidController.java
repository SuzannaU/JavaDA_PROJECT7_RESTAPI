package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Bid;
import com.nnk.springboot.services.BidService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing bids.
 * Handles requests for listing, adding, updating, and deleting bids.
 */
@Controller
@RequestMapping("bid")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    /**
     * Displays the list of all bids.
     * @param model to add to the template
     * @return template for listing all bids
     */
    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("bids", bidService.findAll());
        return "bid/list";
    }

    /**
     * Fetches the add template for a new bid.
     * @param model to add to the template
     * @return template for adding a new bid
     */
    @GetMapping("add")
    public String addBidForm(Model model) {
        Bid bid = new Bid();
        model.addAttribute("bid", bid);
        return "bid/add";
    }

    /**
     * Validates and saves a new bid.
     * @param bid the bid to be added
     * @param result the result of the validation
     * @param model to add to the template
     * @return redirect to the list of bids if successful, otherwise show the add form again
     */
    @PostMapping("validate")
    public String validate(
            @Valid @ModelAttribute("bid") Bid bid,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "bid/add";
        }
        bidService.save(bid);
        model.addAttribute("bids", bidService.findAll());
        return "redirect:/bid/list";
    }

    /**
     * Fetches the update template for a specific bid.
     * @param id the ID of the bid to be updated
     * @param model to add to the template
     * @return template for updating the bid
     */
    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Bid bid = bidService.findById(id);
        model.addAttribute("bid", bid);
        return "bid/update";
    }

    /**
     * Validates and updates an existing bid.
     * @param id the ID of the bid to be updated
     * @param bid the updated bid
     * @param result the result of the validation
     * @param model to add to the template
     * @return redirect to the list of bids if successful, otherwise show the update form again
     */
    @PostMapping("update/{id}")
    public String updateBid(@PathVariable("id") Integer id, @Valid Bid bid,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "bid/update";
        }
        bidService.save(bid);
        model.addAttribute("bids", bidService.findAll());
        return "redirect:/bid/list";
    }

    /**
     * Deletes a specific bid.
     * @param id the ID of the bid to be deleted
     * @param model to add to the template
     * @return redirect to the list of bids
     */
    @GetMapping("delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, Model model) {
        Bid bid = bidService.findById(id);
        bidService.delete(bid);
        model.addAttribute("bids", bidService.findAll());
        return "redirect:/bid/list";
    }
}