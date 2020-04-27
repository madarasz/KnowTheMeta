package com.madarasz.knowthemeta.database.DRs;

import java.util.List;
import java.util.Optional;

import com.madarasz.knowthemeta.database.DOs.Meta;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface MetaRepository extends CrudRepository<Meta, Long> {
    Meta findByTitle(String title);

    Optional<Meta> findById(Long id);

    @Query("MATCH (a:MWL)-[w:MWL]-(m:Meta)-[p:CARDPOOL]-(b:CardPack) RETURN m, p, w ORDER BY b.date_release DESC, a.date_start DESC")
    List<Meta> listMetas();
}