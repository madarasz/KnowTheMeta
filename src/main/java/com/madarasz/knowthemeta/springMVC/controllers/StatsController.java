package com.madarasz.knowthemeta.springMVC.controllers;

import java.util.List;

import com.madarasz.knowthemeta.MetaResults;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DOs.stats.MetaCards;
import com.madarasz.knowthemeta.database.DRs.MetaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StatsController {
    @Autowired MetaResults metaResults;
    @Autowired MetaRepository metaRepository;

    @GetMapping(path = "/stats/{metaTitle}", produces = "application/json")
    @ResponseBody
    public MetaCards getMetaStats(@PathVariable String metaTitle) {
        return metaResults.getCardResultsForMeta(metaTitle);
    }

    @GetMapping(path = "/stats", produces = "application/json")
    @ResponseBody
    public List<Meta> getMetaList() {
        return metaRepository.listMetas();
    }
}