package com.mahmoud.todo.services.urlcheck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeuristicAnalyzerTest {

    private HeuristicAnalyzer heuristicAnalyzer;

    @BeforeEach
    void setUp() {
        heuristicAnalyzer = new HeuristicAnalyzer();
    }

    @Test
    void testSafeUrl() {
        String url = "https://www.google.com";
        int riskScore = heuristicAnalyzer.analyzeUrl(url);
        assertEquals(0, riskScore, "Safe URL should have 0 risk score");
    }

    @Test
    void testUrlWithIpAddress() {
        String url = "http://192.168.1.1/login";
        int riskScore = heuristicAnalyzer.analyzeUrl(url);
        assertTrue(riskScore >= 15, "URL with IP should have risk score >= 15");
    }

    @Test
    void testUrlWithLongDomain() {
        String url = "https://thisdomainhasaverylongnamethatexceedsthirtychars.com";
        int riskScore = heuristicAnalyzer.analyzeUrl(url);
        assertTrue(riskScore >= 10, "URL with long domain should have risk score >= 10");
    }

    @Test
    void testUrlWithSuspiciousKeyword() {
        String url = "https://example.com/login-verify";
        int riskScore = heuristicAnalyzer.analyzeUrl(url);
        assertTrue(riskScore >= 10, "URL with suspicious keyword should have risk score >= 10");
    }

    @Test
    void testUrlWithExcessiveHyphens() {
        String url = "https://fake-bank-login-secure.com";
        int riskScore = heuristicAnalyzer.analyzeUrl(url);
        assertTrue(riskScore >= 10, "URL with excessive hyphens should have risk score >= 10");
    }

    @Test
    void testUrlWithMultipleRiskFactors() {
        // IP + suspicious keyword
        String url = "http://192.168.1.1/secure-bank-login-verify";
        int riskScore = heuristicAnalyzer.analyzeUrl(url);
        assertTrue(riskScore >= 25, "URL with multiple risk factors should have risk score >= 25, got: " + riskScore);
    }

    @Test
    void testComplexUrl() {
        String url = "https://secure.example.com/path/to/resource?param=value";
        int riskScore = heuristicAnalyzer.analyzeUrl(url);
        assertTrue(riskScore >= 10, "URL with 'secure' keyword should have risk score >= 10");
    }
}
