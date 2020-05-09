package com.madarasz.knowthemeta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class AdminStampTests {
    @Autowired
    TimeStamper timeStamper;

    private static final DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    @Transactional
    public void testAdminStamp() throws ParseException {
        // get timestamp, it has not happened yet
        String entryName = "Test entry";
        assertEquals(TimeStamper.MESSAGE_NOT_HAPPENED_YET, timeStamper.getTimeStamp(entryName), "Wrong message");
        // set timestamp, get timestamp, now minus timestamp is less than 3 seconds
        timeStamper.setTimeStamp(entryName);
        String returnedTimeStamp = timeStamper.getTimeStamp(entryName);
        assertNotEquals(TimeStamper.MESSAGE_NOT_HAPPENED_YET, returnedTimeStamp, "Wrong message");
        Date returnedDate = dateTimeFormatter.parse(returnedTimeStamp);
        Date currentDate = new Date();
        assertTrue(currentDate.getTime() - returnedDate.getTime() < 3000, "Wrong timestamp");
        // set timestamp again, get timestamp, elapsed time is less than 3 seconds
        timeStamper.setTimeStamp(entryName);
        String returnedTimeStamp2 = timeStamper.getTimeStamp(entryName);
        assertNotEquals(TimeStamper.MESSAGE_NOT_HAPPENED_YET, returnedTimeStamp2, "Wrong message");
        Date returnedDate2 = dateTimeFormatter.parse(returnedTimeStamp2);
        assertTrue(returnedDate2.getTime() - returnedDate.getTime() < 3000, "Wrong timestamp");
    }
}