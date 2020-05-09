package com.madarasz.knowthemeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.madarasz.knowthemeta.database.DOs.admin.AdminStamp;
import com.madarasz.knowthemeta.database.DRs.AdminStampRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TimeStamper {

    @Autowired AdminStampRepository adminStampRepository;
    public static final String MESSAGE_NOT_HAPPENED_YET = "not happened yet";
    public static final DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public String getTimeStamp(String entry) {
        AdminStamp adminEntry = adminStampRepository.findByEntry(entry);
        if (adminEntry == null) {
            return MESSAGE_NOT_HAPPENED_YET;
        }
        return dateTimeFormatter.format(adminEntry.getTimestamp());
    }

    @Transactional
    public void setTimeStamp(String entry) {
        AdminStamp adminEntry = adminStampRepository.findByEntry(entry);
        if (adminEntry == null) {
            adminEntry = new AdminStamp(entry, new Date());
        } else {
            adminEntry.setTimestamp(new Date());
        }
        adminStampRepository.save(adminEntry);
    }
    
}