package edu.java.scrapper.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractWireMockTest {
    protected static WireMockServer wireMockServer;

    @BeforeAll
    public static void setUpWireMockServer() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    public void resetWireMockServer() {
        wireMockServer.resetAll();
    }

    @AfterAll
    public static void tearDownWireMockServer() {
        wireMockServer.stop();
    }
}
