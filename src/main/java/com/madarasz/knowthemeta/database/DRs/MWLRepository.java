package com.madarasz.knowthemeta.database.DRs;

import java.util.List;
import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.MWL;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface MWLRepository extends CrudRepository<MWL, Long>{
    MWL findByCode(String code);

    Set<MWL> findAll();

    @Query("MATCH (m:MWL) RETURN m ORDER BY m.date_start DESC LIMIT 1")
    MWL findLast();

    @Query("MATCH (m:MWL) RETURN m ORDER BY m.date_start DESC")
    List<MWL> listMWLs();

    // for testing
    @Query("MATCH (c:Card {title:$0}), (m:MWL {code:$1}) RETURN EXISTS((c)-[:MWL_CARD]-(m))")
    Boolean verifyCardInMWLRelationship(String cardTitle, String mwlCode);
}