package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Bid;
import com.nnk.springboot.services.BidService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("bid")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("bids", bidService.findAll());
        return "bid/list";
    }

    @GetMapping("add")
    public String addBidForm(Model model) {
        Bid bid = new Bid();
        model.addAttribute("bid", bid);
        return "bid/add";
    }

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

    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Bid bid = bidService.findById(id);
        model.addAttribute("bid", bid);
        return "bid/update";
    }

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

    @GetMapping("delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, Model model) {
        Bid bid = bidService.findById(id);
        bidService.delete(bid);
        model.addAttribute("bids", bidService.findAll());
        return "redirect:/bid/list";
    }
}