package com.madarasz.knowthemeta.database.DRs;

import com.madarasz.knowthemeta.database.DOs.admin.AdminStamp;

import org.springframework.data.repository.CrudRepository;

public interface AdminStampRepository extends CrudRepository<AdminStamp, Long> {
    AdminStamp findByEntry(String entry);
}