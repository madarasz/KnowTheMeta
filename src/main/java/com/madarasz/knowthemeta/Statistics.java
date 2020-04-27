package com.madarasz.knowthemeta;

import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.MWL;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.MWLRepository;
import com.madarasz.knowthemeta.database.DRs.MetaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Statistics {

    @Autowired MetaRepository metaRepository;
    @Autowired MWLRepository mwlRepository;
    @Autowired CardPackRepository cardPackRepository;
    private final static Logger log = LoggerFactory.getLogger(Statistics.class);

    @Transactional
    public void addMeta(String mwlCode, String packCode, Boolean newCards, String title) {
        MWL mwl = mwlRepository.findByCode(mwlCode);
        CardPack cardPack = cardPackRepository.findByCode(packCode);
        if (mwl == null) log.error("No MWL found by code: " + mwlCode);
        if (cardPack == null) log.error("No CardPack found by code: " + packCode);
        Meta meta = new Meta(cardPack, mwl, newCards, title);
        metaRepository.save(meta);
        log.info(String.format("New meta added: %s (%s - %s)", title, packCode, mwlCode));
    }

    @Transactional
    public void deleteMeta(Long id) {
        Meta meta = metaRepository.findById(id).get();
        if (meta == null) {
            log.error("Meta not found: " + id);
        } else {
            log.info("Deleting meta: " + meta.getTitle());
            metaRepository.delete(meta);
            // TODO: destroy related tournaments, matches, etc.
        }
    }
}