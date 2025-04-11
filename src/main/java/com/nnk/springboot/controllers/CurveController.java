package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("curvePoint")
public class CurveController {

    private final CurvePointRepository curvePointRepository;

    public CurveController(CurvePointRepository curvePointRepository) {
        this.curvePointRepository = curvePointRepository;
    }

    @RequestMapping("list")
    public String home(Model model) {
        model.addAttribute("curvePoints", curvePointRepository.findAll());
        return "curvePoint/list";
    }

    @GetMapping("add")
    public String addCurveForm(Model model) {
        CurvePoint curvePoint = new CurvePoint();
        model.addAttribute("curvePoint", curvePoint);
        return "curvePoint/add";
    }

    @PostMapping("validate")
    public String validate(
            @Valid @ModelAttribute("curvePoint") CurvePoint curvePoint,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "curvePoint/add";
        }
        curvePointRepository.save(curvePoint);
        model.addAttribute("curvePoints", curvePointRepository.findAll());
        return "redirect:/curvePoint/list";
    }

    @GetMapping("update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        CurvePoint curvePoint = curvePointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid CurvePoint Id: " + id));
        model.addAttribute("curvePoint", curvePoint);
        return "curvePoint/update";
    }

    @PostMapping("update/{id}")
    public String updateCurve(@PathVariable("id") Integer id, @Valid CurvePoint curvePoint,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "curvePoint/update";
        }
        curvePointRepository.save(curvePoint);
        model.addAttribute("curvePoints", curvePointRepository.findAll());
        return "redirect:/curvePoint/list";
    }

    @GetMapping("delete/{id}")
    public String deleteCurve(@PathVariable("id") Integer id, Model model) {
        CurvePoint curvePoint = curvePointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid CurvePoint Id: " + id));
        curvePointRepository.delete(curvePoint);
        model.addAttribute("curvePoints", curvePointRepository.findAll());
        return "redirect:/curvePoint/list";
    }
}
