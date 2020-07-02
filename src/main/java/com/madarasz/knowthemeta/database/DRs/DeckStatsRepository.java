package com.madarasz.knowthemeta.database.DRs;

import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.stats.DeckStats;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface DeckStatsRepository extends CrudRepository<DeckStats, Long> {
    @Query("MATCH (d:Deck)-[r1:DECK]-(s:DeckStats)-[r0:DECKSTAT]-(i:DeckIdentity)-[r2:META]-(m:Meta {title: $metaTitle}) RETURN d,s,m,i,r0,r1,r2")
    Set<DeckStats> findByMetaTitle(String metaTitle);
}