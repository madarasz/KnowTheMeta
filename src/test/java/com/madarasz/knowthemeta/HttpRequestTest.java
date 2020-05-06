package com.madarasz.knowthemeta;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {
    @MockBean
    private Operations operations;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testPageTitle() {
        assertTrue(this.restTemplate.getForObject("http://localhost:" + port + "/", String.class).contains("Admin page"));
    }

    @Test
    public void testNetrunnerDBStamp() throws Exception {
        // setup
        doReturn((double)3).when(operations).updateFromNetrunnerDB();
        // run
        assertTrue(this.restTemplate.getForObject("http://localhost:" + port + "/load-netrunnerdb", String.class).contains("Updated from NetrunnerDB"));
    }
}