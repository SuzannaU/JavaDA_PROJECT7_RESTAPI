package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Bid;
import com.nnk.springboot.repositories.BidRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("bid")
public class BidController {

    private final BidRepository bidRepository;

    public BidController(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("bids", bidRepository.findAll());
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
        bidRepository.save(bid);
        model.addAttribute("bids", bidRepository.findAll());
        return "redirect:/bid/list";
    }

    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Bid bid = bidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bid Id: " + id));
        model.addAttribute("bid", bid);
        return "bid/update";
    }

    @PostMapping("update/{id}")
    public String updateBid(@PathVariable("id") Integer id, @Valid Bid bid,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "bid/update";
        }
        bidRepository.save(bid);
        model.addAttribute("bids", bidRepository.findAll());
        return "redirect:/bid/list";
    }

    @GetMapping("delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, Model model) {
        Bid bid = bidRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bid Id: " + id));
        bidRepository.delete(bid);
        model.addAttribute("bids", bidRepository.findAll());
        return "redirect:/bid/list";
    }
}