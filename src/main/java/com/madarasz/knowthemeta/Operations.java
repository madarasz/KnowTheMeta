package com.madarasz.knowthemeta;

import java.util.Set;

import com.madarasz.knowthemeta.brokers.NetrunnerDBBroker;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Operations {

    @Autowired NetrunnerDBBroker netrunnerDBBroker;
    @Autowired CardCycleRepository cardCycleRepository;
    @Autowired CardPackRepository cardPackRepository;

    private static final Logger log = LoggerFactory.getLogger(Operations.class);

    public void updateFromNetrunnerDB() {
        this.updateCycles();
        this.updatePacks();
        netrunnerDBBroker.loadCards();
    }

    private void updatePacks() {
        Set<CardPack> packs = netrunnerDBBroker.loadPacks();
        int createCount = 0;
        for (CardPack cardPack : packs) {
            CardPack found = cardPackRepository.findByCode(cardPack.getCode());
            if (found == null) {
                cardPackRepository.save(cardPack);
                createCount++;
            } 
        }
        if (createCount == 0) {
            log.info("Cardpacks: no updates");
        } else {
            log.info(String.format("Cardpacks: %d added", createCount));
        }
    }

    private void updateCycles() {
        Set<CardCycle> cycles = netrunnerDBBroker.loadCycles();
        int updateCount = 0;
        int createCount = 0;
        for (CardCycle cardCycle : cycles) {
            CardCycle found = cardCycleRepository.findByCode(cardCycle.getCode());
            if (found == null) {
                cardCycleRepository.save(cardCycle);
                createCount++;
            } else {
                if (!found.equals(cardCycle)) {
                    // TODO: update
                    updateCount++;
                    log.error("UPDATE NOT IMPLEMENTED");
                }
            }
        }
        if (updateCount + createCount == 0) {
            log.info("Cardcycles: no updates");
        } else {
            log.info(String.format("Cardcycles: %d added, %d updated", createCount, updateCount));
        }
    }

}