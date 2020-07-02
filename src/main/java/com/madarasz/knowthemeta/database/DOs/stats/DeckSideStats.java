package com.madarasz.knowthemeta.database.DOs.stats;

import java.util.ArrayList;
import java.util.List;

public class DeckSideStats {
    private List<DeckIdentity> runner = new ArrayList<>();
    private List<DeckIdentity> corp = new ArrayList<>();

    public DeckSideStats() {
    }

    public List<DeckIdentity> getRunner() {
        return runner;
    }

    public void setRunner(List<DeckIdentity> runner) {
        this.runner = runner;
    }

    public List<DeckIdentity> getCorp() {
        return corp;
    }

    public void setCorp(List<DeckIdentity> corp) {
        this.corp = corp;
    }
    
}