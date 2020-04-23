package com.madarasz.knowthemeta.database.DRs;

import com.madarasz.knowthemeta.database.DOs.CardPack;

import org.springframework.data.repository.CrudRepository;

public interface CardPackRepository extends CrudRepository<CardPack, Long>{
    CardPack findByCode(String code);
}