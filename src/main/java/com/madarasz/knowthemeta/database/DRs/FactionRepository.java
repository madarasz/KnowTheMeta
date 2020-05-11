package com.madarasz.knowthemeta.database.DRs;

import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.Faction;

import org.springframework.data.repository.CrudRepository;

public interface FactionRepository extends CrudRepository<Faction, Long> {
    Faction findByFactionCode(String factionCode);
    Set<Faction> findAll();
}