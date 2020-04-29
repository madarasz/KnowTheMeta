package com.madarasz.knowthemeta.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestHelper {

    public String getTestResource(String fileName) {
        File file = new File("src/test/resources/" + fileName);
        String result = "";
        try {
            result = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}