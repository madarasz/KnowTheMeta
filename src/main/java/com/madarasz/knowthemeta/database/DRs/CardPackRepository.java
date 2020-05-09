package com.madarasz.knowthemeta.database.DRs;

import java.util.List;
import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.CardPack;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface CardPackRepository extends CrudRepository<CardPack, Long>{
    CardPack findByCode(String code);

    Set<CardPack> findAll();

    @Query("MATCH (c:CardCycle)-[:CYCLE]-(p:CardPack) RETURN p ORDER BY c.position DESC, p.position DESC LIMIT 1")
    CardPack findLast();

    @Query("MATCH (c:CardCycle)-[:CYCLE]-(p:CardPack) RETURN p ORDER BY c.position DESC, p.position DESC")
    List<CardPack> listPacks();

    @Query("MATCH (c:CardCycle {code:$cycleCode})-[:CYCLE]-(p:CardPack) RETURN p ORDER BY c.position DESC, p.position DESC")
    List<CardPack> listByCycleCode(String cycleCode);

    // for testing
    @Query("MATCH (c:CardCycle {code:$0}), (p:CardPack {code:$1}) RETURN EXISTS((c)-[:CYCLE]-(p))")
    Boolean verifyPackInCycleRelationship(String cycleCode, String packCode);
}