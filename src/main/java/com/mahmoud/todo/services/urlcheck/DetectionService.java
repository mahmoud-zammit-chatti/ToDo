package com.mahmoud.todo.services.urlcheck;

import com.mahmoud.todo.DTOs.urlcheck.UrlCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DetectionService {

    private static final Logger logger = LoggerFactory.getLogger(DetectionService.class);
    private static final int MALICIOUS_THRESHOLD = 60;

    private final HeuristicAnalyzer heuristicAnalyzer;
    private final SafeBrowsingClient safeBrowsingClient;
    private final VirusTotalClient virusTotalClient;

    public DetectionService(HeuristicAnalyzer heuristicAnalyzer,
                          SafeBrowsingClient safeBrowsingClient,
                          VirusTotalClient virusTotalClient) {
        this.heuristicAnalyzer = heuristicAnalyzer;
        this.safeBrowsingClient = safeBrowsingClient;
        this.virusTotalClient = virusTotalClient;
    }

    public UrlCheckResponse checkUrl(String url) {
        logger.info("Checking URL: {}", url);
        
        int totalRiskScore = 0;
        List<String> sources = new ArrayList<>();
        
        // 1. Check Google Safe Browsing
        boolean googleFlagged = false;
        try {
            googleFlagged = safeBrowsingClient.isMalicious(url);
            if (googleFlagged) {
                totalRiskScore += 100; // Immediate malicious
                sources.add("Google Safe Browsing");
                logger.info("Google Safe Browsing flagged URL as malicious");
            }
        } catch (Exception e) {
            logger.error("Google Safe Browsing check failed: {}", e.getMessage());
        }
        
        // 2. Check VirusTotal
        int virusTotalDetections = 0;
        try {
            virusTotalDetections = virusTotalClient.getMaliciousCount(url);
            if (virusTotalDetections > 0) {
                // Add risk score based on detections (cap at 80)
                totalRiskScore += Math.min(virusTotalDetections * 10, 80);
                sources.add("VirusTotal");
                logger.info("VirusTotal found {} detections", virusTotalDetections);
            }
        } catch (Exception e) {
            logger.error("VirusTotal check failed: {}", e.getMessage());
        }
        
        // 3. Heuristic analysis
        int heuristicScore = 0;
        try {
            heuristicScore = heuristicAnalyzer.analyzeUrl(url);
            totalRiskScore += heuristicScore;
            if (heuristicScore > 0) {
                logger.info("Heuristic analysis score: {}", heuristicScore);
            }
        } catch (Exception e) {
            logger.error("Heuristic analysis failed: {}", e.getMessage());
        }
        
        // Determine status
        String status;
        Boolean redirectToGame = null;
        
        if (totalRiskScore > MALICIOUS_THRESHOLD) {
            status = "MALICIOUS";
            redirectToGame = true;
            logger.warn("URL classified as MALICIOUS with risk score: {}", totalRiskScore);
        } else {
            status = "SAFE";
            logger.info("URL classified as SAFE with risk score: {}", totalRiskScore);
        }
        
        return new UrlCheckResponse(status, totalRiskScore, sources, redirectToGame);
    }
}
