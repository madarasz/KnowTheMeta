package com.madarasz.knowthemeta.springMVC.controllers;

import com.madarasz.knowthemeta.Operations;
import com.madarasz.knowthemeta.Statistics;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardRepository;
import com.madarasz.knowthemeta.database.DRs.MWLRepository;
import com.madarasz.knowthemeta.database.DRs.MetaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AdminController {

    @Autowired Operations operations;
    @Autowired CardCycleRepository cardCycleRepository;
    @Autowired CardPackRepository cardPackRepository;
    @Autowired CardRepository cardRepository;
    @Autowired CardInPackRepository cardInPackRepository;
    @Autowired MWLRepository mwlRepository;
    @Autowired MetaRepository metaRepository;
    @Autowired Statistics statistics;

    private static final String STAMP_NETRUNNERDB_UPDATE = "NetrunnerDB update";
    private final static Logger log = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/")
    public String adminPage(Model model) {
        long cycleCount = cardCycleRepository.count();
        long packCount = cardPackRepository.count();
        long cardCount = cardRepository.count();
        long printCount = cardInPackRepository.count();
        long mwlCount = mwlRepository.count();
        model.addAttribute("cycleCount", cycleCount);
        model.addAttribute("packCount", packCount);
        model.addAttribute("cardCount", cardCount);
        model.addAttribute("printCount", printCount);
        model.addAttribute("mwlCount", mwlCount);
        model.addAttribute("lastCycle", cycleCount > 0 ? cardCycleRepository.findLast().getName() : "none");
        model.addAttribute("lastPack", packCount > 0 ? cardPackRepository.findLast().getName() : "none");
        model.addAttribute("lastPrint", cardCount > 0 ? cardRepository.findLast().getTitle() : "none");
        model.addAttribute("lastMWL", mwlCount > 0 ? mwlRepository.findLast().getName() : "none");
        model.addAttribute("stampNetrunnerDB", operations.getTimeStamp(STAMP_NETRUNNERDB_UPDATE));
        model.addAttribute("packs", cardPackRepository.listPacks());
        model.addAttribute("mwls", mwlRepository.listMWLs());
        model.addAttribute("metas", metaRepository.listMetas());
        return "admin";
    }

    @GetMapping("/load-netrunnerdb")
    public RedirectView loadNetrunnerDB(RedirectAttributes redirectAttributes) {
        operations.updateFromNetrunnerDB();
        redirectAttributes.addFlashAttribute("message", "Updated from NetrunnerDB");
        operations.setTimeStamp(STAMP_NETRUNNERDB_UPDATE);
        return new RedirectView("/");
    }

    @GetMapping("/add-meta")
    public RedirectView addMeta(@RequestParam(name = "metaTitle") String title, @RequestParam(name = "metaMWL") String mwlCode, 
                                @RequestParam(name = "metaPack") String packCode, @RequestParam(name = "metaNewCards", required = false) Boolean newCards, 
                                RedirectAttributes redirectAttributes) {
        if (newCards == null) newCards = false; // if checkbox was not checked
        statistics.addMeta(mwlCode, packCode, newCards, title);
        redirectAttributes.addFlashAttribute("message", "Meta added");
        return new RedirectView("/");
    }
}