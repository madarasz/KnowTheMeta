package com.madarasz.knowthemeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.madarasz.knowthemeta.brokers.ABRBroker;
import com.madarasz.knowthemeta.brokers.NetrunnerDBBroker;
import com.madarasz.knowthemeta.database.DOs.CardCycle;
import com.madarasz.knowthemeta.database.DOs.CardPack;
import com.madarasz.knowthemeta.database.DOs.MWL;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DOs.Tournament;
import com.madarasz.knowthemeta.database.DOs.admin.AdminStamp;
import com.madarasz.knowthemeta.database.DRs.AdminStampRepository;
import com.madarasz.knowthemeta.database.DRs.CardCycleRepository;
import com.madarasz.knowthemeta.database.DRs.CardPackRepository;
import com.madarasz.knowthemeta.database.DRs.MWLRepository;
import com.madarasz.knowthemeta.database.DRs.TournamentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Component
public class Operations {

    @Autowired NetrunnerDBBroker netrunnerDBBroker;
    @Autowired ABRBroker abrBroker;
    @Autowired CardCycleRepository cardCycleRepository;
    @Autowired CardPackRepository cardPackRepository;
    @Autowired AdminStampRepository adminStampRepository;
    @Autowired MWLRepository mwlRepository;
    @Autowired TournamentRepository tournamentRepository;

    private static final Logger log = LoggerFactory.getLogger(Operations.class);
    private static final StopWatch stopwatch = new StopWatch();
    private static final DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public String getTimeStamp(String entry) {
        AdminStamp adminEntry = adminStampRepository.findByEntry(entry);
        if (adminEntry == null) {
            return "not happened yet";
        }
        return dateTimeFormatter.format(adminEntry.getTimestamp());
    }

    @Transactional
    public void setTimeStamp(String entry) {
        AdminStamp adminEntry = adminStampRepository.findByEntry(entry);
        if (adminEntry == null) {
            adminEntry = new AdminStamp(entry, new Date());
        } else {
            adminEntry.setTimestamp(new Date());
        }
        adminStampRepository.save(adminEntry);
    }

    public double updateFromNetrunnerDB() {
        log.info("Starting NetrunnerDB update");
        StopWatch netrunnerTimer = new StopWatch();
        netrunnerTimer.start();

        this.updateCycles();
        this.updatePacks();
        netrunnerDBBroker.loadCards();
        this.updateMWLs();

        netrunnerTimer.stop();
        log.info(String.format("Finished NetrunnerDB update (%.3f sec)", netrunnerTimer.getTotalTimeSeconds()));
        return netrunnerTimer.getTotalTimeSeconds();
    }

    @Transactional
    private void updatePacks() {
        stopwatch.start();
        Set<CardPack> packs = netrunnerDBBroker.loadPacks();
        int createCount = 0;

        for (CardPack cardPack : packs) {
            CardPack found = cardPackRepository.findByCode(cardPack.getCode());
            if (found == null) {
                cardPackRepository.save(cardPack);
                CardCycle cardCycle = cardPack.getCycle();
                cardCycle.addPack(cardPack);
                cardCycleRepository.save(cardCycle);
                log.debug(String.format("New pack: %s (cycle: %s)", cardPack.getName(), cardPack.getCycle().getName()));
                createCount++;
            } 
        }

        // logging
        stopwatch.stop();
        if (createCount == 0) {
            log.info(String.format("Cardpacks: no updates (%.3f sec)", stopwatch.getTotalTimeSeconds()));
        } else {
            log.info(String.format("Cardpacks: %d added (%.3f sec)", createCount, stopwatch.getTotalTimeSeconds()));
        }
    }

    @Transactional
    private void updateCycles() {
        stopwatch.start();
        Set<CardCycle> cycles = netrunnerDBBroker.loadCycles();
        int updateCount = 0;
        int createCount = 0;

        for (CardCycle cardCycle : cycles) {
            CardCycle found = cardCycleRepository.findByCode(cardCycle.getCode());
            if (found == null) {
                cardCycleRepository.save(cardCycle);
                log.debug("New cycle: " + cardCycle.getName());
                createCount++;
            } else {
                if (!found.equals(cardCycle)) {
                    // TODO: update
                    updateCount++;
                    log.error("UPDATE NOT IMPLEMENTED");
                }
            }
        }

        // logging
        stopwatch.stop();
        if (updateCount + createCount == 0) {
            log.info(String.format("Cardcycles: no updates (%.3f sec)", stopwatch.getTotalTimeSeconds()));
        } else {
            log.info(String.format("Cardcycles: %d added, %d updated (%.3f sec)", createCount, updateCount, stopwatch.getTotalTimeSeconds()));
        }
    }

    @Transactional
    private void updateMWLs() {
        stopwatch.start();
        Set<MWL> mwls = netrunnerDBBroker.loadMWL();
        int createCount = 0;
        int updateCount = 0;

        for (MWL mwl : mwls) {
            MWL existing = mwlRepository.findByCode(mwl.getCode());
            if (existing == null) {
                // add MWL
                mwlRepository.save(mwl);
                createCount++;
            } else {
                // update if active property is different
                if (existing.getActive() != mwl.getActive()) {
                    existing.setActive(mwl.getActive());
                    mwlRepository.save(existing);
                    updateCount++;
                }
            }
        }

        // logging
        stopwatch.stop();
        if (updateCount + createCount == 0) {
            log.info(String.format("MWL: no updates (%.3f sec)", stopwatch.getTotalTimeSeconds()));
        } else {
            log.info(String.format("MWL: %d added, %d updated (%.3f sec)", createCount, updateCount, stopwatch.getTotalTimeSeconds()));
        }
    }

    @Transactional
    public String getMetaData(Meta meta) {
        stopwatch.start();
        int tournamentCreatedCount = 0;

        List<Tournament> tournaments = abrBroker.getTournamentData(meta);
        for (Tournament tournament : tournaments) {
            Tournament found = tournamentRepository.findById(tournament.getId());
            if (found == null) {
                // new tournament
                log.debug("New tournament saved: " + tournament.toString());
                tournamentCreatedCount++;
            }
        }
        // logging
        stopwatch.stop();
        String message = String.format("Meta update \"%s\" finished (%.3f sec) - New tournament: %d", meta.getTitle(), 
            stopwatch.getTotalTimeSeconds(), tournamentCreatedCount);
        log.info(message);
        return message;
    }
}