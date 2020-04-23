package com.madarasz.knowthemeta.springMVC.controllers;

import com.madarasz.knowthemeta.Operations;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AdminController {

    @Autowired Operations operations;
    @Autowired CardCycleRepository cardCycleRepository;
    @Autowired CardPackRepository cardPackRepository;

    @GetMapping("/")
    public String adminPage(Model model) {
        model.addAttribute("cycleCount", cardCycleRepository.count());
        model.addAttribute("packCount", cardPackRepository.count());
        return "admin";
    }

    @GetMapping("/load-netrunnerdb")
    public RedirectView loadNetrunnerDB(RedirectAttributes redirectAttributes) {
        operations.updateFromNetrunnerDB();
        redirectAttributes.addFlashAttribute("message", "yolo");
        return new RedirectView("/");
    }
}