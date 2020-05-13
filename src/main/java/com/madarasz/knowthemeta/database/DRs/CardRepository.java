package com.madarasz.knowthemeta.database.DRs;

import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.Card;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface CardRepository extends CrudRepository<Card, Long> {
    Card findByTitle(String title);
    
    @Query("MATCH (c:Card)-[:CARD_IN_PACK {code:$code}]-(:CardPack) return c LIMIT 1")
    Card findByCode(String code);

    @Query("MATCH (c:Card {title:$0})-[r:CARD_IN_PACK]-(:CardPack) return r.code ORDER BY r.code DESC LIMIT 1")
    String getLastCode(String cardTitle);

    @Query("MATCH (c:Card)-[r:CARD_IN_PACK]-(:CardPack) return c ORDER BY r.code DESC LIMIT 1")
    Card findLast();

    @Query("MATCH (c:Card)-[:CARD_IN_PACK]-(:CardPack) return c")
    Set<Card> listAll();
}