package com.madarasz.knowthemeta.database.DRs;

import java.util.List;
import java.util.Optional;

import com.madarasz.knowthemeta.database.DOs.Meta;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface MetaRepository extends CrudRepository<Meta, Long> {
    Meta findByTitle(String title);

    Optional<Meta> findById(Long id);

    @Query("MATCH (a:MWL)-[w:MWL]-(m:Meta)-[p:CARDPOOL]-(b:CardPack) RETURN m, p, w, b, a ORDER BY b.date_release DESC, a.date_start DESC")
    List<Meta> listMetas();

    @Query("MATCH (t:Tournament)-[:META]-(m:Meta {title:$0}) RETURN COUNT(t)")
    int countTournaments(String title);

    // filter out neurtal standings
    @Query("MATCH (f:Faction)-[:FACTION]-(c:Card)-[:IDENTITY]-(s:Standing)-[:TOURNAMENT]-(:Tournament)-[:META]-(m:Meta {title:$0}) WHERE (NOT(f.factionCode CONTAINS 'neutral')) RETURN COUNT(s)")
    int countStandings(String title);

    @Query("MATCH (d:Deck)-[:DECK]-(:Standing {isRunner: true})-[:TOURNAMENT]-(:Tournament)-[:META]-(m:Meta {title:$0}) RETURN COUNT(d)")
    int countRunnerDecks(String title);

    @Query("MATCH (d:Deck)-[:DECK]-(:Standing {isRunner: false})-[:TOURNAMENT]-(:Tournament)-[:META]-(m:Meta {title:$0}) RETURN COUNT(d)")
    int countCorpDecks(String title);

    @Query("MATCH (s:Standing)-[:TOURNAMENT]-(:Tournament)-[:META]-(m:Meta {title:$0}) RETURN SUM(s.winCount+s.lossCount+s.drawCount)")
    int countMatches(String title);

    @Query("MATCH (m:Meta {title:$0}) OPTIONAL MATCH (s:Standing)-[:TOURNAMENT]-(t:Tournament)-[:META]-(m) DETACH DELETE s,t,m")
    void deleteMeta(String title);
}