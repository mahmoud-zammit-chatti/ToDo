package com.mahmoud.todo.services.urlcheck;

import com.mahmoud.todo.DTOs.urlcheck.UrlCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetectionServiceTest {

    @Mock
    private HeuristicAnalyzer heuristicAnalyzer;

    @Mock
    private SafeBrowsingClient safeBrowsingClient;

    @Mock
    private VirusTotalClient virusTotalClient;

    private DetectionService detectionService;

    @BeforeEach
    void setUp() {
        detectionService = new DetectionService(heuristicAnalyzer, safeBrowsingClient, virusTotalClient);
    }

    @Test
    void testCheckUrl_Safe() {
        // Arrange
        when(safeBrowsingClient.isMalicious(anyString())).thenReturn(false);
        when(virusTotalClient.getMaliciousCount(anyString())).thenReturn(0);
        when(heuristicAnalyzer.analyzeUrl(anyString())).thenReturn(0);

        // Act
        UrlCheckResponse response = detectionService.checkUrl("https://www.google.com");

        // Assert
        assertEquals("SAFE", response.getStatus());
        assertEquals(0, response.getRiskScore());
        assertTrue(response.getSources().isEmpty());
        assertNull(response.getRedirectToGame());
    }

    @Test
    void testCheckUrl_MaliciousFromGoogleSafeBrowsing() {
        // Arrange
        when(safeBrowsingClient.isMalicious(anyString())).thenReturn(true);
        when(virusTotalClient.getMaliciousCount(anyString())).thenReturn(0);
        when(heuristicAnalyzer.analyzeUrl(anyString())).thenReturn(0);

        // Act
        UrlCheckResponse response = detectionService.checkUrl("http://malicious.com");

        // Assert
        assertEquals("MALICIOUS", response.getStatus());
        assertTrue(response.getRiskScore() >= 100);
        assertTrue(response.getSources().contains("Google Safe Browsing"));
        assertTrue(response.getRedirectToGame());
    }

    @Test
    void testCheckUrl_MaliciousFromVirusTotal() {
        // Arrange
        when(safeBrowsingClient.isMalicious(anyString())).thenReturn(false);
        when(virusTotalClient.getMaliciousCount(anyString())).thenReturn(8); // 8 detections = 80 score
        when(heuristicAnalyzer.analyzeUrl(anyString())).thenReturn(0);

        // Act
        UrlCheckResponse response = detectionService.checkUrl("http://suspicious.com");

        // Assert
        assertEquals("MALICIOUS", response.getStatus());
        assertEquals(80, response.getRiskScore());
        assertTrue(response.getSources().contains("VirusTotal"));
        assertTrue(response.getRedirectToGame());
    }

    @Test
    void testCheckUrl_MaliciousFromHeuristics() {
        // Arrange
        when(safeBrowsingClient.isMalicious(anyString())).thenReturn(false);
        when(virusTotalClient.getMaliciousCount(anyString())).thenReturn(0);
        when(heuristicAnalyzer.analyzeUrl(anyString())).thenReturn(65); // Above threshold

        // Act
        UrlCheckResponse response = detectionService.checkUrl("http://192.168.1.1/secure-bank-login");

        // Assert
        assertEquals("MALICIOUS", response.getStatus());
        assertEquals(65, response.getRiskScore());
        assertTrue(response.getRedirectToGame());
    }

    @Test
    void testCheckUrl_CombinedRiskScore() {
        // Arrange
        when(safeBrowsingClient.isMalicious(anyString())).thenReturn(false);
        when(virusTotalClient.getMaliciousCount(anyString())).thenReturn(3); // 30 score
        when(heuristicAnalyzer.analyzeUrl(anyString())).thenReturn(40); // 40 score

        // Act
        UrlCheckResponse response = detectionService.checkUrl("http://suspicious.com");

        // Assert
        assertEquals("MALICIOUS", response.getStatus());
        assertEquals(70, response.getRiskScore()); // 30 + 40 = 70
        assertTrue(response.getRedirectToGame());
    }

    @Test
    void testCheckUrl_BelowThreshold() {
        // Arrange - Total score = 50, below 60 threshold
        when(safeBrowsingClient.isMalicious(anyString())).thenReturn(false);
        when(virusTotalClient.getMaliciousCount(anyString())).thenReturn(2); // 20 score
        when(heuristicAnalyzer.analyzeUrl(anyString())).thenReturn(30); // 30 score

        // Act
        UrlCheckResponse response = detectionService.checkUrl("http://example.com");

        // Assert
        assertEquals("SAFE", response.getStatus());
        assertEquals(50, response.getRiskScore());
        assertNull(response.getRedirectToGame());
    }

    @Test
    void testCheckUrl_GracefulFailureHandling() {
        // Arrange - All external checks throw exceptions
        when(safeBrowsingClient.isMalicious(anyString())).thenThrow(new RuntimeException("API error"));
        when(virusTotalClient.getMaliciousCount(anyString())).thenThrow(new RuntimeException("API error"));
        when(heuristicAnalyzer.analyzeUrl(anyString())).thenReturn(15); // Only heuristic works

        // Act
        UrlCheckResponse response = detectionService.checkUrl("http://example.com");

        // Assert
        assertEquals("SAFE", response.getStatus());
        assertEquals(15, response.getRiskScore());
        assertNull(response.getRedirectToGame());
    }
}
