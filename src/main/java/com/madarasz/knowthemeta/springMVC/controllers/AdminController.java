package com.madarasz.knowthemeta.springMVC.controllers;

import com.madarasz.knowthemeta.Operations;
import com.madarasz.knowthemeta.Statistics;
import com.madarasz.knowthemeta.brokers.NetrunnerDBBroker;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.CardRepository;
import com.madarasz.knowthemeta.database.DRs.DeckRepository;
import com.madarasz.knowthemeta.database.DRs.MWLRepository;
import com.madarasz.knowthemeta.database.DRs.MetaRepository;
import com.madarasz.knowthemeta.database.DRs.StandingRepository;
import com.madarasz.knowthemeta.database.DRs.TournamentRepository;
import com.madarasz.knowthemeta.database.DRs.UserRepository;

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
    @Autowired TournamentRepository tournamentRepository;
    @Autowired StandingRepository standingRepository;
    @Autowired DeckRepository deckRepository;
    @Autowired UserRepository userRepository;
    @Autowired Statistics statistics;
    @Autowired NetrunnerDBBroker netrunnerDBBroker;

    private static final String STAMP_NETRUNNERDB_UPDATE = "NetrunnerDB update";

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
        model.addAttribute("tournamentCount", tournamentRepository.count());
        model.addAttribute("standingCount", standingRepository.count());
        model.addAttribute("deckCount", deckRepository.count());
        model.addAttribute("playerCount", userRepository.count());
        return "admin";
    }

    @GetMapping("/load-netrunnerdb")
    public RedirectView loadNetrunnerDB(RedirectAttributes redirectAttributes) {
        double timer = operations.updateFromNetrunnerDB();
        redirectAttributes.addFlashAttribute("message", String.format("Updated from NetrunnerDB (%.3f sec)", timer));
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

    @GetMapping("/delete-meta")
    public RedirectView deleteMeta(@RequestParam(name = "id") Long id, RedirectAttributes redirectAttributes) {
        statistics.deleteMeta(id);
        redirectAttributes.addFlashAttribute("message", "Meta deleted");
        return new RedirectView("/");
    }

    @GetMapping("/get-meta")
    public RedirectView getMeta(@RequestParam(name = "id") Long id, RedirectAttributes redirectAttributes) {
        Meta meta = metaRepository.findById(id).get();
        String message = operations.getMetaData(meta);
        redirectAttributes.addFlashAttribute("message", message);
        return new RedirectView("/");
    }

    // Temporary testing solution
    @GetMapping("/temp")
    public RedirectView temp(RedirectAttributes redirectAttributes) {
        //netrunnerDBBroker.loadDeck(59695); // temporary testing task
        redirectAttributes.addFlashAttribute("message", "temp task run");
        return new RedirectView("/");
    }
}