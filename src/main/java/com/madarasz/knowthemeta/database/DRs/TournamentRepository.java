package com.madarasz.knowthemeta.database.DRs;

import java.util.List;

import com.madarasz.knowthemeta.database.DOs.Tournament;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface TournamentRepository extends CrudRepository<Tournament, Long> {
    Tournament findById(int id);

    @Query("MATCH (m)-[:META]-(t:Tournament) WHERE ID(m)=$metaId RETURN t")
    List<Tournament> listForMeta(Long metaId);
}