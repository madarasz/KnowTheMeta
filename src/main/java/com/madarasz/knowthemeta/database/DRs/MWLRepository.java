package com.madarasz.knowthemeta.database.DRs;

import java.util.List;

import com.madarasz.knowthemeta.database.DOs.MWL;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface MWLRepository extends CrudRepository<MWL, Long>{
    MWL findByCode(String code);

    @Query("MATCH (m:MWL) RETURN m ORDER BY m.date_start DESC LIMIT 1")
    MWL findLast();

    @Query("MATCH (m:MWL) RETURN m ORDER BY m.date_start DESC")
    List<MWL> listMWLs();
}