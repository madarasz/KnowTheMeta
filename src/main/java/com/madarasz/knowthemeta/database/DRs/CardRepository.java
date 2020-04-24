package com.madarasz.knowthemeta.database.DRs;

import com.madarasz.knowthemeta.database.DOs.Card;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface CardRepository extends CrudRepository<Card, Long> {
    Card findByTitle(String title);
    
    @Query("MATCH (c:Card)-[:CARD_IN_PACK {code:$code}]-(:CardPack) return c LIMIT 1")
    Card findByCode(String code);
}