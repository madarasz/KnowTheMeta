package com.madarasz.knowthemeta.springMVC.controllers;

import com.madarasz.knowthemeta.Operations;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardRepository;

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
    @Autowired CardRepository cardRepository;
    @Autowired CardInPackRepository cardInPackRepository;

    private static final String STAMP_NETRUNNERDB_UPDATE = "NetrunnerDB update";

    @GetMapping("/")
    public String adminPage(Model model) {
        model.addAttribute("cycleCount", cardCycleRepository.count());
        model.addAttribute("packCount", cardPackRepository.count());
        model.addAttribute("cardCount", cardRepository.count());
        model.addAttribute("printCount", cardInPackRepository.count());
        model.addAttribute("lastCycle", cardCycleRepository.findLast().getName());
        model.addAttribute("lastPack", cardPackRepository.findLast().getName());
        model.addAttribute("lastPrint", cardRepository.findLast().getTitle());
        model.addAttribute("stampNetrunnerDB", operations.getTimeStamp(STAMP_NETRUNNERDB_UPDATE));
        return "admin";
    }

    @GetMapping("/load-netrunnerdb")
    public RedirectView loadNetrunnerDB(RedirectAttributes redirectAttributes) {
        operations.updateFromNetrunnerDB();
        redirectAttributes.addFlashAttribute("message", "Updated from NetrunnerDB");
        operations.setTimeStamp(STAMP_NETRUNNERDB_UPDATE);
        return new RedirectView("/");
    }
}