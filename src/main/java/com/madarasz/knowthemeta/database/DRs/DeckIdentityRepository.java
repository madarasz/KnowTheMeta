package com.madarasz.knowthemeta.database.DRs;

import java.util.Set;

import com.madarasz.knowthemeta.database.DOs.stats.DeckIdentity;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;

public interface DeckIdentityRepository extends CrudRepository<DeckIdentity, Long> {
    @Query("MATCH (d:Deck)-[r1:DECK]-(s:DeckStats)-[r0:DECKSTAT]-(i:DeckIdentity)-[r2:META]-(m:Meta {title: $metaTitle}), " + 
        "(i)-[r3:IDENTITY]-(c:Card)-[r4:FACTION]-(f:Faction), (c:Card)-[r5:CARD_IN_PACK]-(p:CardPack), (d)-[r6:PLAYER]-(u:User) RETURN d,s,m,i,c,f,p,u,r0,r1,r2,r3,r4,r5,r6")
    Set<DeckIdentity> findByMetaTitle(String metaTitle);
}