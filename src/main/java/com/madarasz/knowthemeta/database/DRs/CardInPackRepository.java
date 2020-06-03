package com.madarasz.knowthemeta.database.DRs;

import java.util.List;
import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface CardInPackRepository extends CrudRepository<CardInPack, Long>{
    @Query("MATCH (n:Card {type_code:\"identity\"})-[r:CARD_IN_PACK]-(p:CardPack) RETURN n, p, r")
    Set<CardInPack> listIdentities();

    @Query("MATCH (n:Card)-[r:CARD_IN_PACK]-(p:CardPack) RETURN p, n, r")
    Set<CardInPack> listAll();

    @Query("MATCH (n:Card {title: $title})-[r:CARD_IN_PACK]-(p:CardPack)-[r2:CYCLE]-(y:CardCycle) RETURN p, n, r, r2, y ORDER BY y.position DESC, p.position DESC")
    List<CardInPack> findAllByTitle(String title);

    // for testing
    @Query("MATCH (:Card)-[c:CARD_IN_PACK {code:$0}]-(:CardPack) RETURN c LIMIT 1")
    CardInPack findByCode(String code);
    @Query("MATCH (c:Card {title:$0})-[:CARD_IN_PACK {code:$1}]-(p:CardPack) RETURN EXISTS((c)-[:CARD_IN_PACK]-(p))")
    Boolean verifyCardInPackRelationship(String cardTitle, String cardCode);
}