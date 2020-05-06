package com.madarasz.knowthemeta.database.DRs;

import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.CardCycle;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface CardCycleRepository extends CrudRepository<CardCycle, Long>{
    CardCycle findByCode(String code);

    @Query("MATCH (c:CardCycle) RETURN c ORDER BY c.position DESC LIMIT 1")
    CardCycle findLast();

    @Query("MATCH (c:CardCycle) RETURN c")
    Set<CardCycle> findall();
}