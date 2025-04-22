package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.services.CurvePointService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing curvePoints.
 * Handles requests for listing, adding, updating, and deleting curvePoints.
 */
@Controller
@RequestMapping("curvePoint")
public class CurveController {
    private static final Logger logger = LoggerFactory.getLogger(CurveController.class);

    private final CurvePointService curvePointService;

    public CurveController(CurvePointService curvePointService) {
        this.curvePointService = curvePointService;
    }

    /**
     * Displays the list of all curvePoints.
     *
     * @param model to add to the template
     * @return template for listing all curvePoints
     */
    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("curvePoints", curvePointService.findAll());
        return "curvePoint/list";
    }

    /**
     * Fetches the add template for a new curvePoint.
     *
     * @param model to add to the template
     * @return template for adding a new curvePoint
     */
    @GetMapping("add")
    public String addCurveForm(Model model) {
        CurvePoint curvePoint = new CurvePoint();
        model.addAttribute("curvePoint", curvePoint);
        return "curvePoint/add";
    }

    /**
     * Validates and saves a new curvePoint.
     *
     * @param curvePoint the curvePoint to be added
     * @param result     the result of the validation
     * @param model      to add to the template
     * @return redirect to the list of curvePoints if successful, otherwise show the add form again
     */
    @PostMapping("validate")
    public String validate(
            @Valid @ModelAttribute("curvePoint") CurvePoint curvePoint,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            logger.error("error with submitted CurvePoint");
            return "curvePoint/add";
        }
        curvePointService.save(curvePoint);
        model.addAttribute("curvePoints", curvePointService.findAll());
        return "redirect:/curvePoint/list";
    }

    /**
     * Fetches the update template for a specific curvePoint.
     *
     * @param id    the ID of the curvePoint to be updated
     * @param model to add to the template
     * @return template for updating the curvePoint
     */
    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        CurvePoint curvePoint = curvePointService.findById(id);
        model.addAttribute("curvePoint", curvePoint);
        return "curvePoint/update";
    }

    /**
     * Validates and updates an existing curvePoint.
     *
     * @param id         the ID of the curvePoint to be updated
     * @param curvePoint the updated curvePoint
     * @param result     the result of the validation
     * @param model      to add to the template
     * @return redirect to the list of curvePoints if successful, otherwise show the update form again
     */
    @PostMapping("update/{id}")
    public String updateCurve(@PathVariable("id") Integer id, @Valid CurvePoint curvePoint,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "curvePoint/update";
        }
        curvePointService.save(curvePoint);
        model.addAttribute("curvePoints", curvePointService.findAll());
        return "redirect:/curvePoint/list";
    }

    /**
     * Deletes a specific curvePoint.
     *
     * @param id    the ID of the curvePoint to be deleted
     * @param model to add to the template
     * @return redirect to the list of curvePoints
     */
    @GetMapping("delete/{id}")
    public String deleteCurve(@PathVariable("id") Integer id, Model model) {
        CurvePoint curvePoint = curvePointService.findById(id);
        curvePointService.delete(curvePoint);
        model.addAttribute("curvePoints", curvePointService.findAll());
        return "redirect:/curvePoint/list";
    }
}
