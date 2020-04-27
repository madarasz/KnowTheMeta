package com.madarasz.knowthemeta.database.DRs;

import com.madarasz.knowthemeta.database.DOs.Tournament;
import org.springframework.data.repository.CrudRepository;

public interface TournamentRepository extends CrudRepository<Tournament, Long> {
    Tournament findById(int id);
}