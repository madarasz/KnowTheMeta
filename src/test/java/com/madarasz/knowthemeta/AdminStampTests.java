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
    Operations operations;

    private static final DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    @Transactional
    public void testAdminStamp() throws ParseException {
        String entryName = "Test entry";
        assertEquals(operations.MESSAGE_NOT_HAPPENED_YET, operations.getTimeStamp(entryName), "Wrong message");
        operations.setTimeStamp(entryName);
        String returnedTimeStamp = operations.getTimeStamp(entryName);
        assertNotEquals(operations.MESSAGE_NOT_HAPPENED_YET, returnedTimeStamp, "Wrong message");
        Date returnedDate = dateTimeFormatter.parse(returnedTimeStamp);
        Date currentDate = new Date();
        assertTrue(currentDate.getTime() - returnedDate.getTime() < 1000, "Wrong timestamp");
    }
}