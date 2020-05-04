package com.madarasz.knowthemeta.database.DRs;

import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface CardInPackRepository extends CrudRepository<CardInPack, Long>{
    // for testing
    @Query("MATCH (:Card)-[c:CARD_IN_PACK {code:$0}]-(:CardPack) return c LIMIT 1")
    CardInPack findByCode(String code);
    @Query("MATCH (c:Card {title:$0})-[:CARD_IN_PACK {code:$1}]-(p:CardPack) RETURN EXISTS((c)-[:CARD_IN_PACK]-(p))")
    Boolean verifyCardInPackRelationship(String cardTitle, String cardCode);
}