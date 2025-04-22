package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.services.TradeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing trades.
 * Handles requests for listing, adding, updating, and deleting trades.
 */
@Controller
@RequestMapping("trade")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    /**
     * Displays the list of all trades.
     *
     * @param model to add to the template
     * @return template for listing all trades
     */
    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("trades", tradeService.findAll());
        return "trade/list";
    }

    /**
     * Fetches the add template for a new trade.
     *
     * @param model to add to the template
     * @return template for adding a new trade
     */
    @GetMapping("add")
    public String addTradeForm(Model model) {
        Trade trade = new Trade();
        model.addAttribute("trade", trade);
        return "trade/add";
    }

    /**
     * Validates and saves a new trade.
     *
     * @param trade  the trade to be added
     * @param result the result of the validation
     * @param model  to add to the template
     * @return redirect to the list of trades if successful, otherwise show the add form again
     */
    @PostMapping("validate")
    public String validate(
            @Valid @ModelAttribute("trade") Trade trade,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "trade/add";
        }
        tradeService.save(trade);
        model.addAttribute("trades", tradeService.findAll());
        return "redirect:/trade/list";
    }

    /**
     * Fetches the update template for a specific trade.
     *
     * @param id    the ID of the trade to be updated
     * @param model to add to the template
     * @return template for updating the trade
     */
    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Trade trade = tradeService.findById(id);
        model.addAttribute("trade", trade);
        return "trade/update";
    }

    /**
     * Validates and updates an existing trade.
     *
     * @param id     the ID of the trade to be updated
     * @param trade  the updated trade
     * @param result the result of the validation
     * @param model  to add to the template
     * @return redirect to the list of trades if successful, otherwise show the update form again
     */
    @PostMapping("update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @Valid Trade trade,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "trade/update";
        }
        tradeService.save(trade);
        model.addAttribute("trades", tradeService.findAll());
        return "redirect:/trade/list";
    }

    /**
     * Deletes a specific trade.
     *
     * @param id    the ID of the trade to be deleted
     * @param model to add to the template
     * @return redirect to the list of trades
     */
    @GetMapping("delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, Model model) {
        Trade trade = tradeService.findById(id);
        tradeService.delete(trade);
        model.addAttribute("trades", tradeService.findAll());
        return "redirect:/trade/list";
    }
}
