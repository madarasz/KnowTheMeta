package com.madarasz.knowthemeta.database.DRs;

import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.Standing;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface StandingRepository extends CrudRepository<Standing, Long> {

    @Query("MATCH (s:Standing {isRunner: $isRunner, rank: $rank})-[:TOURNAMENT]-(:Tournament {id:$0}) RETURN s LIMIT 1")
    Standing findByTournamentSideRank(int tournamentId, boolean isRunner, int rank);

    @Query("MATCH (s:Standing)-[:TOURNAMENT]-(:Tournament {id: $0}) OPTIONAL MATCH (d:Deck)-[:DECK]-(s) RETURN s, d")
    Set<Standing> findByTournament(int tournamentId);

    @Query("MATCH (f:Faction)-[r1:FACTION]-(i:Card)-[r2:IDENTITY]-(s:Standing)-[r3:TOURNAMENT]-(t:Tournament)-[r4:META]-(m:Meta {title:$0}) RETURN f,i,s,t,m,r1,r2,r3,r4")
    Set<Standing> findByMeta(String metaTitle);

    @Query("MATCH (s:Standing)-[r3:TOURNAMENT]-(t:Tournament)-[r4:META]-(m:Meta {title:$0}) WHERE s.isRunner = true RETURN SUM(s.winCount)")
    int countRunnerWinsInMeta(String metaTitle);
    @Query("MATCH (s:Standing)-[r3:TOURNAMENT]-(t:Tournament)-[r4:META]-(m:Meta {title:$0}) WHERE s.isRunner = true RETURN SUM(s.lossCount)")
    int countRunnerLossesInMeta(String metaTitle);
    @Query("MATCH (s:Standing)-[r3:TOURNAMENT]-(t:Tournament)-[r4:META]-(m:Meta {title:$0}) WHERE s.isRunner = true RETURN SUM(s.drawCount)")
    int countRunnerDrawsInMeta(String metaTitle);
    @Query("MATCH (:Deck)-[:DECK]-(s:Standing)-[r3:TOURNAMENT]-(t:Tournament)-[r4:META]-(m:Meta {title:$0}) WHERE s.isRunner = true RETURN SUM(s.winCount)")
    int countRunnerWinsWithDeckInMeta(String metaTitle);
    @Query("MATCH (:Deck)-[:DECK]-(s:Standing)-[r3:TOURNAMENT]-(t:Tournament)-[r4:META]-(m:Meta {title:$0}) WHERE s.isRunner = true RETURN SUM(s.winCount+s.lossCount+s.drawCount)")
    int countRunnerAllWithDeckInMeta(String metaTitle);
    @Query("MATCH (:Deck)-[:DECK]-(s:Standing)-[r3:TOURNAMENT]-(t:Tournament)-[r4:META]-(m:Meta {title:$0}) WHERE s.isRunner = false RETURN SUM(s.winCount)")
    int countCorpWinsWithDeckInMeta(String metaTitle);
    @Query("MATCH (:Deck)-[:DECK]-(s:Standing)-[r3:TOURNAMENT]-(t:Tournament)-[r4:META]-(m:Meta {title:$0}) WHERE s.isRunner = false RETURN SUM(s.winCount+s.lossCount+s.drawCount)")
    int countCorpAllWithDeckInMeta(String metaTitle);

    @Query("MATCH (c:Card {title: $cardTitle})-[r1:CARD_IN_DECK]-(d:Deck)-[r4:DECK]-(s:Standing)-[r2:TOURNAMENT]-(t:Tournament)-[r3:META]-(m:Meta {title:$0}) RETURN c,s,t,m,d,r1,r2,r3,r4")
    Set<Standing> findByMetaAndCard(String metaTitle, String cardTitle);

    @Query("MATCH (f2:Faction)-[r8:FACTION]-(i:Card {title: $identityTitle})-[r1:IDENTITY]-(d:Deck)-[r2:DECK]-(s:Standing)-[r3:TOURNAMENT]-(t:Tournament)-[r4:META]-(m:Meta {title: $metaTitle}), " +
            "(d)-[r5:CARD_IN_DECK]-(c:Card)-[r7:FACTION]-(f1:Faction), (d)-[r6:PLAYER]-(u:User) RETURN d,s,t,i,c,u,f1,f2,r1,r2,r3,r4,r5,r6,r7,r8")
    Set<Standing> findWithDecksByMetaAndID(String metaTitle, String identityTitle);
}