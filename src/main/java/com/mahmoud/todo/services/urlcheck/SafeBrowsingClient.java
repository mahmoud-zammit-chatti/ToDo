package com.mahmoud.todo.services.urlcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SafeBrowsingClient {

    private static final Logger logger = LoggerFactory.getLogger(SafeBrowsingClient.class);
    private static final String SAFE_BROWSING_API_URL = 
        "https://safebrowsing.googleapis.com/v4/threatMatches:find";

    @Value("${google.safebrowsing.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public SafeBrowsingClient() {
        this.restTemplate = new RestTemplate();
    }

    public boolean isMalicious(String url) {
        // If API key is not configured, return false (safe)
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("Google Safe Browsing API key not configured. Skipping check.");
            return false;
        }

        try {
            String requestUrl = SAFE_BROWSING_API_URL + "?key=" + apiKey;
            
            // Build request body
            Map<String, Object> requestBody = buildRequestBody(url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            Map<String, Object> responseBody = response.getBody();
            
            // If matches found, URL is malicious
            if (responseBody != null && responseBody.containsKey("matches")) {
                logger.info("Google Safe Browsing detected threat for URL: {}", url);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error checking URL with Google Safe Browsing: {}", e.getMessage());
            return false; // Fail open - don't block if API fails
        }
    }

    private Map<String, Object> buildRequestBody(String url) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // Client info
        Map<String, String> client = new HashMap<>();
        client.put("clientId", "cybersecurity-awareness-app");
        client.put("clientVersion", "1.0.0");
        requestBody.put("client", client);
        
        // Threat info
        Map<String, Object> threatInfo = new HashMap<>();
        threatInfo.put("threatTypes", List.of(
            "MALWARE",
            "SOCIAL_ENGINEERING",
            "UNWANTED_SOFTWARE",
            "POTENTIALLY_HARMFUL_APPLICATION"
        ));
        threatInfo.put("platformTypes", List.of("ANY_PLATFORM"));
        threatInfo.put("threatEntryTypes", List.of("URL"));
        
        Map<String, String> urlEntry = new HashMap<>();
        urlEntry.put("url", url);
        threatInfo.put("threatEntries", List.of(urlEntry));
        
        requestBody.put("threatInfo", threatInfo);
        
        return requestBody;
    }
}
