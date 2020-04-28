package com.madarasz.knowthemeta.database.DRs;

import com.madarasz.knowthemeta.database.DOs.Standing;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface StandingRepository extends CrudRepository<Standing, Long> {

    @Query("MATCH (s:Standing {isRunner: $isRunner, rank: $rank})-[:TOURNAMENT]-(:Tournament {id: $tournamentId}) RETURN s LIMIT 1")
    Standing findByTournamentSideRank(int tournamentId, boolean isRunner, int rank);
}