package com.mahmoud.todo.services.urlcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
public class VirusTotalClient {

    private static final Logger logger = LoggerFactory.getLogger(VirusTotalClient.class);
    private static final String VIRUSTOTAL_API_URL = "https://www.virustotal.com/api/v3/urls/";

    @Value("${virustotal.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public VirusTotalClient() {
        this.restTemplate = new RestTemplate();
    }

    public int getMaliciousCount(String url) {
        // If API key is not configured, return 0 (no malicious detections)
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("VirusTotal API key not configured. Skipping check.");
            return 0;
        }

        try {
            // VirusTotal uses URL ID (base64 encoded URL without padding)
            String urlId = encodeUrlId(url);
            String requestUrl = VIRUSTOTAL_API_URL + urlId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-apikey", apiKey);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                entity,
                Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody != null && responseBody.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                if (data.containsKey("attributes")) {
                    Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
                    if (attributes.containsKey("last_analysis_stats")) {
                        Map<String, Object> stats = (Map<String, Object>) attributes.get("last_analysis_stats");
                        
                        int malicious = getIntValue(stats, "malicious");
                        int suspicious = getIntValue(stats, "suspicious");
                        
                        int totalDetections = malicious + suspicious;
                        
                        if (totalDetections > 0) {
                            logger.info("VirusTotal detected {} malicious/suspicious detections for URL: {}", 
                                totalDetections, url);
                        }
                        
                        return totalDetections;
                    }
                }
            }
            
            return 0;
        } catch (Exception e) {
            logger.error("Error checking URL with VirusTotal: {}", e.getMessage());
            return 0; // Fail open - don't block if API fails
        }
    }

    private String encodeUrlId(String url) {
        // VirusTotal URL ID is base64(url) without padding
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(url.getBytes());
        return encoded;
    }

    private int getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }
}
