package com.madarasz.knowthemeta.database.DRs;

import com.madarasz.knowthemeta.database.DOs.Standing;
import org.springframework.data.repository.CrudRepository;

public interface StandingRepository extends CrudRepository<Standing, Long> {
}