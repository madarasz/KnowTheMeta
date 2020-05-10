package com.madarasz.knowthemeta.database.DRs;

import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.Standing;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface StandingRepository extends CrudRepository<Standing, Long> {

    @Query("MATCH (s:Standing {isRunner: $isRunner, rank: $rank})-[:TOURNAMENT]-(:Tournament {id:$0}) RETURN s LIMIT 1")
    Standing findByTournamentSideRank(int tournamentId, boolean isRunner, int rank);

    @Query("MATCH (s:Standing)-[:TOURNAMENT]-(:Tournament {id: $0}) RETURN s")
    Set<Standing> findByTournament(int tournamentId);
}