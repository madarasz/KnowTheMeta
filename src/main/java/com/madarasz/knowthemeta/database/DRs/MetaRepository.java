package com.madarasz.knowthemeta.database.DRs;

import java.util.List;

import com.madarasz.knowthemeta.database.DOs.Meta;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface MetaRepository extends CrudRepository<Meta, Long> {
    Meta findByTitle(String title);

    @Query("MATCH (:MWL)-[w:MWL]-(m:Meta)-[p:CARDPOOL]-(:CardPack) RETURN m, p, w ORDER BY w.date_start DESC")
    List<Meta> listMetas();
}