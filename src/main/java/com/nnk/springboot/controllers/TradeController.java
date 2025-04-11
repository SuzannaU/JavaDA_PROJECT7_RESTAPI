package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("trade")
public class TradeController {

    private final TradeRepository tradeRepository;

    public TradeController(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("trades", tradeRepository.findAll());
        return "trade/list";
    }

    @GetMapping("add")
    public String addTradeForm(Model model) {
        Trade trade = new Trade();
        model.addAttribute("trade", trade);
        return "trade/add";
    }

    @PostMapping("validate")
    public String validate(
            @Valid @ModelAttribute("trade") Trade trade,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "trade/add";
        }
        tradeRepository.save(trade);
        model.addAttribute("trades", tradeRepository.findAll());
        return "redirect:/trade/list";
    }

    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Trade Id: " + id));
        model.addAttribute("trade", trade);
        return "trade/update";
    }

    @PostMapping("update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @Valid Trade trade,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "trade/update";
        }
        tradeRepository.save(trade);
        model.addAttribute("trades", tradeRepository.findAll());
        return "redirect:/trade/list";
    }

    @GetMapping("delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, Model model) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Trade Id: " + id));
        tradeRepository.delete(trade);
        model.addAttribute("trades", tradeRepository.findAll());
        return "redirect:/trade/list";
    }
}
