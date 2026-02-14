package com.mahmoud.todo.services.urlcheck;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class HeuristicAnalyzer {

    private static final Pattern IP_ADDRESS_PATTERN = 
        Pattern.compile("^(https?://)?(\\d{1,3}\\.){3}\\d{1,3}");
    
    private static final String[] SUSPICIOUS_KEYWORDS = {
        "login", "verify", "secure", "bank", "update", "signin", 
        "account", "password", "suspend", "confirm"
    };

    public int analyzeUrl(String url) {
        int riskScore = 0;
        
        // Check if URL contains IP address instead of domain
        if (IP_ADDRESS_PATTERN.matcher(url.toLowerCase()).find()) {
            riskScore += 15;
        }
        
        // Extract domain for analysis
        String domain = extractDomain(url);
        
        // Check domain length
        if (domain != null && domain.length() > 30) {
            riskScore += 10;
        }
        
        // Check for suspicious keywords
        String lowerUrl = url.toLowerCase();
        for (String keyword : SUSPICIOUS_KEYWORDS) {
            if (lowerUrl.contains(keyword)) {
                riskScore += 10;
                break; // Only count once
            }
        }
        
        // Check for excessive hyphens in domain
        if (domain != null && countOccurrences(domain, '-') > 3) {
            riskScore += 10;
        }
        
        return riskScore;
    }
    
    private String extractDomain(String url) {
        try {
            // Remove protocol
            String urlWithoutProtocol = url.replaceFirst("^https?://", "");
            // Extract domain (before first slash or colon)
            int slashIndex = urlWithoutProtocol.indexOf('/');
            int colonIndex = urlWithoutProtocol.indexOf(':');
            
            int endIndex = urlWithoutProtocol.length();
            if (slashIndex > 0) endIndex = Math.min(endIndex, slashIndex);
            if (colonIndex > 0) endIndex = Math.min(endIndex, colonIndex);
            
            return urlWithoutProtocol.substring(0, endIndex);
        } catch (Exception e) {
            return null;
        }
    }
    
    private int countOccurrences(String str, char ch) {
        return (int) str.chars().filter(c -> c == ch).count();
    }
}
