package com.madarasz.knowthemeta.springMVC.controllers;

import com.madarasz.knowthemeta.MetaResults;
import com.madarasz.knowthemeta.database.DOs.stats.MetaCards;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StatsController {
    @Autowired MetaResults metaResults;

    @GetMapping(path = "/stats/{metaTitle}", produces = "application/json")
    @ResponseBody
    public MetaCards getMetaStats(@PathVariable String metaTitle) {
        return metaResults.getCardResultsForMeta(metaTitle);
    }
}