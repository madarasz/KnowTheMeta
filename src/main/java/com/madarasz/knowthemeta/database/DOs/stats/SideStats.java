package com.madarasz.knowthemeta.database.DOs.stats;

import java.util.ArrayList;
import java.util.List;

public class SideStats {
    private List<WinRateUsedCounter> runner = new ArrayList<WinRateUsedCounter>();
    private List<WinRateUsedCounter> corporation = new ArrayList<WinRateUsedCounter>();

    public SideStats() {
    }

    public List<WinRateUsedCounter> getRunner() {
        return runner;
    }

    public void setRunner(List<WinRateUsedCounter> runner) {
        this.runner = runner;
    }

    public List<WinRateUsedCounter> getCorp() {
        return corporation;
    }

    public void setCorp(List<WinRateUsedCounter> corp) {
        this.corporation = corp;
    }

    public void addRunner(WinRateUsedCounter stat) {
        runner.add(stat);
    }

    public void addCorp(WinRateUsedCounter stat) {
        corporation.add(stat);
    }
}