package com.madarasz.knowthemeta.database.DRs;

import com.madarasz.knowthemeta.database.DOs.Card;

import org.springframework.data.repository.CrudRepository;

public interface CardRepository extends CrudRepository<Card, Long> {

}