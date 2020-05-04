package com.madarasz.knowthemeta.database.DRs;

import java.util.List;
import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.Card;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface CardRepository extends CrudRepository<Card, Long> {
    Card findByTitle(String title);
    
    @Query("MATCH (c:Card)-[:CARD_IN_PACK {code:$code}]-(:CardPack) return c LIMIT 1")
    Card findByCode(String code);

    @Query("MATCH (c:Card)-[r:CARD_IN_PACK]-(:CardPack) return c ORDER BY r.code DESC LIMIT 1")
    Card findLast();

    @Query("MATCH (c:Card)-[:CARD_IN_PACK]-(:CardPack) return c")
    Set<Card> listAll();

    @Query("MATCH (n:Card {type_code:\"identity\"})-[p:CARD_IN_PACK]-(:CardPack) RETURN p")
    List<CardInPack> listIdentities();

    @Query("MATCH (n:Card)-[r:CARD_IN_PACK]-(p:CardPack) RETURN p, n, r")
    List<CardInPack> listCardInPack();
}