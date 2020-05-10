package com.madarasz.knowthemeta;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.madarasz.knowthemeta.brokers.ABRBroker;
import com.madarasz.knowthemeta.database.DOs.Deck;
import com.madarasz.knowthemeta.database.DOs.Meta;
import com.madarasz.knowthemeta.database.DOs.Standing;
import com.madarasz.knowthemeta.database.DOs.Tournament;
import com.madarasz.knowthemeta.database.DOs.User;
import com.madarasz.knowthemeta.database.DOs.relationships.CardInPack;
import com.madarasz.knowthemeta.database.DRs.CardInPackRepository;
import com.madarasz.knowthemeta.database.DRs.DeckRepository;
import com.madarasz.knowthemeta.database.DRs.MetaRepository;
import com.madarasz.knowthemeta.database.DRs.StandingRepository;
import com.madarasz.knowthemeta.database.DRs.TournamentRepository;
import com.madarasz.knowthemeta.database.DRs.UserRepository;
import com.madarasz.knowthemeta.helper.Searcher;
import com.madarasz.knowthemeta.helper.TestData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit test for GetMetaData of MetaOperations
 */
@ExtendWith(MockitoExtension.class)
public class MetaOperationsGetMetaDataTests {

    @Mock ABRBroker abrBroker;
    @Mock CardInPackRepository cardInPackRepository;
    @Mock TournamentRepository tournamentRepository;
    @Mock StandingRepository standingRepository;
    @Mock UserRepository userRepository;
    @Mock DeckRepository deckRepository;
    @Mock MetaRepository metaRepository;
    @Spy Searcher searcher; // TODO: this integration is still in
    @Spy Meta meta; // TODO: this integration is still in
    @InjectMocks MetaOperations operations;
    private static final TestData testData = new TestData();

    @Test
    public void testGetMetaData() {
        // setup
        Set<CardInPack> cards = testData.cardInPackTestSet.stream().collect(Collectors.toSet());
        Mockito.when(abrBroker.getTournamentData(any(Meta.class))).thenReturn(testData.testTournamentList);
        Mockito.when(abrBroker.getStadingData(any(Tournament.class), any(), any(), any())).thenReturn(testData.testStandingSet);
        Mockito.when(cardInPackRepository.listAll()).thenReturn(cards);
        Mockito.when(cardInPackRepository.listIdentities()).thenReturn(cards);
        Mockito.when(tournamentRepository.listForMeta(any())).thenReturn(new ArrayList<Tournament>());
        Mockito.when(userRepository.listAll()).thenReturn(new ArrayList<User>());
        Mockito.when(deckRepository.listAll()).thenReturn(new HashSet<Deck>());
        // run
        operations.getMetaData(testData.testMeta);
        // verify
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
        verify(standingRepository, times(2)).save(any(Standing.class));
        verify(deckRepository, times(1)).save(any(Deck.class));
    }

    @Test
    public void testGetMetaDataNoUpdate() {
        // setup
        List<User> userList = new ArrayList<User>();
        userList.add(testData.testPlayer);
        Set<Deck> deckList = new HashSet<Deck>();
        deckList.add(testData.testDeck);
        Set<CardInPack> cards = testData.cardInPackTestSet.stream().collect(Collectors.toSet());
        Mockito.when(abrBroker.getTournamentData(any(Meta.class))).thenReturn(testData.testTournamentList);
        Mockito.when(abrBroker.getStadingData(any(Tournament.class), any(), any(), any())).thenReturn(testData.testStandingSet);
        Mockito.when(cardInPackRepository.listAll()).thenReturn(cards);
        Mockito.when(cardInPackRepository.listIdentities()).thenReturn(cards);
        Mockito.when(tournamentRepository.listForMeta(any())).thenReturn(testData.testTournamentList);
        Mockito.when(userRepository.listAll()).thenReturn(userList);
        Mockito.when(deckRepository.listAll()).thenReturn(deckList);
        Mockito.when(searcher.getStadingByRankSide(anyCollection(), anyInt(), anyBoolean())).thenReturn(testData.testStanding1);
        // run
        operations.getMetaData(testData.testMeta);
        // verify
        verify(tournamentRepository, times(0)).save(any(Tournament.class));
        verify(standingRepository, times(0)).save(any(Standing.class));
        verify(deckRepository, times(0)).save(any(Deck.class));
    }

}