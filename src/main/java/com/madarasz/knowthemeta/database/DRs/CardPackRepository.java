package com.madarasz.knowthemeta.database.DRs;

import java.util.List;

import com.madarasz.knowthemeta.database.DOs.CardPack;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface CardPackRepository extends CrudRepository<CardPack, Long>{
    CardPack findByCode(String code);

    @Query("MATCH (c:CardCycle)-[:CYCLE]-(p:CardPack) RETURN p ORDER BY c.position DESC, p.position DESC LIMIT 1")
    CardPack findLast();

    @Query("MATCH (c:CardCycle)-[:CYCLE]-(p:CardPack) RETURN p ORDER BY c.position DESC, p.position DESC")
    List<CardPack> listPacks();
}