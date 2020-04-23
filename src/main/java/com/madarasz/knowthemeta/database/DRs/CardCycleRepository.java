package com.madarasz.knowthemeta.database.DRs;

import com.madarasz.knowthemeta.database.DOs.CardCycle;

import org.springframework.data.repository.CrudRepository;

public interface CardCycleRepository extends CrudRepository<CardCycle, Long>{
    CardCycle findByCode(String code);
}