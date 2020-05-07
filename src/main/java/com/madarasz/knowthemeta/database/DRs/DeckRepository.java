package com.madarasz.knowthemeta.database.DRs;

import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.Deck;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface DeckRepository extends CrudRepository<Deck, Long> {
    Deck findById(int id);

    @Query("MATCH (d:Deck)-[p:PLAYER]-(:User) RETURN d, p")
    Set<Deck> listAll();
}