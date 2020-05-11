package com.madarasz.knowthemeta.brokers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HttpBroker {
    private final static Logger log = LoggerFactory.getLogger(HttpBroker.class);
    
    public JsonElement readJSONFromURL(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection request = url.openConnection();
            request.connect();
            BufferedReader json = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            JsonElement root = JsonParser.parseReader(json);
            json.close();
            return root;
        } catch (Exception ex) {
            log.error("logged exception", ex);
            return null;
        } 
    }
}