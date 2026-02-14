# Cybersecurity Awareness URL Checker API

A Spring Boot backend service for checking URLs against security threats as part of a cybersecurity awareness mobile application.

## 🎯 Overview

This application provides a REST API endpoint that analyzes URLs for potential security threats using:
- **Google Safe Browsing API** - Checks for malware, social engineering, and harmful applications
- **VirusTotal API** - Aggregates results from multiple antivirus engines
- **Heuristic Analysis** - Local pattern-based detection for suspicious URL characteristics

When a malicious URL is detected, the response includes a flag to redirect users to an awareness game module.

## ✨ Features

- ✅ Stateless REST API (no database required)
- ✅ Multi-source threat detection
- ✅ Risk scoring algorithm
- ✅ Graceful API failure handling
- ✅ CORS enabled for mobile frontend
- ✅ Clean layered architecture
- ✅ Comprehensive logging

## 🛠 Tech Stack

- Java 17
- Spring Boot 3.5.7
- Spring Security
- Maven

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL (for existing TODO features, not used by URL checker)
- API Keys:
  - Google Safe Browsing API key (optional)
  - VirusTotal API key (optional)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/mahmoud-zammit-chatti/ToDo.git
cd ToDo
```

### 2. Configure API Keys

Edit `src/main/resources/application.properties` and add your API keys:

```properties
# Google Safe Browsing API Key
# Get your key at: https://developers.google.com/safe-browsing/v4/get-started
google.safebrowsing.api.key=YOUR_GOOGLE_API_KEY_HERE

# VirusTotal API Key
# Get your key at: https://www.virustotal.com/gui/my-apikey
virustotal.api.key=YOUR_VIRUSTOTAL_API_KEY_HERE
```

> **Note:** The API works without keys but will only use heuristic analysis. For best results, configure both API keys.

### 3. Build the Application

```bash
./mvnw clean install
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The server will start on `http://localhost:8080`

## 📡 API Endpoint

### Check URL for Threats

**Endpoint:** `POST /api/v1/check-url`

**Request Body:**
```json
{
  "url": "https://example.com"
}
```

**Response (SAFE):**
```json
{
  "status": "SAFE",
  "riskScore": 0,
  "sources": [],
  "redirectToGame": null
}
```

**Response (MALICIOUS):**
```json
{
  "status": "MALICIOUS",
  "riskScore": 85,
  "sources": ["Google Safe Browsing", "VirusTotal"],
  "redirectToGame": true
}
```

### Example curl Request

```bash
# Check a safe URL
curl -X POST http://localhost:8080/api/v1/check-url \
  -H "Content-Type: application/json" \
  -d '{"url": "https://www.google.com"}'

# Expected response:
# {"status":"SAFE","riskScore":0,"sources":[],"redirectToGame":null}

# Check a suspicious URL with heuristic triggers
curl -X POST http://localhost:8080/api/v1/check-url \
  -H "Content-Type: application/json" \
  -d '{"url": "http://192.168.1.1/secure-bank-login"}'

# Expected response (with heuristics only):
# {"status":"SAFE","riskScore":25,"sources":[],"redirectToGame":null}
# Note: Risk score 25 = IP address(15) + suspicious keyword(10)

# Check a URL with excessive hyphens and long domain
curl -X POST http://localhost:8080/api/v1/check-url \
  -H "Content-Type: application/json" \
  -d '{"url": "https://very-suspicious-phishing-website-with-many-hyphens-domain.com/login"}'

# Expected response:
# {"status":"SAFE","riskScore":40,"sources":[],"redirectToGame":null}
# Note: Risk score 40 = long domain(10) + suspicious keyword(10) + excessive hyphens(10) + suspicious keyword again(10)

# To test with real threat detection, add your API keys to application.properties
```

### Testing Without API Keys

The application works without API keys but will only use heuristic analysis:
- IP addresses in URLs
- Long domain names
- Suspicious keywords
- Excessive hyphens

To test the full multi-source detection, you'll need to configure the API keys.

## 🔍 How Risk Scoring Works

### Detection Sources

1. **Google Safe Browsing**
   - Checks for: MALWARE, SOCIAL_ENGINEERING, UNWANTED_SOFTWARE, POTENTIALLY_HARMFUL_APPLICATION
   - If flagged: +100 risk score (immediate MALICIOUS classification)

2. **VirusTotal**
   - Aggregates results from 70+ antivirus engines
   - Risk score: (malicious + suspicious detections) × 10 (capped at 80)

3. **Heuristic Analysis** (built-in, always active)
   - IP address instead of domain: +15
   - Domain length > 30 characters: +10
   - Suspicious keywords (login, verify, secure, bank, etc.): +10
   - More than 3 hyphens in domain: +10

### Classification Logic

- **Risk Score > 60** → Status: `MALICIOUS`, `redirectToGame: true`
- **Risk Score ≤ 60** → Status: `SAFE`, `redirectToGame: null`

### Failure Handling

- If external APIs fail or are unavailable, the system gracefully falls back to heuristic-only detection
- No blocking occurs on API failures (fail-open approach for availability)

## 🏗 Architecture

```
src/main/java/com/mahmoud/todo/
├── controllers/
│   └── UrlCheckController.java       # REST endpoint
├── services/urlcheck/
│   ├── DetectionService.java         # Main orchestration service
│   ├── SafeBrowsingClient.java       # Google Safe Browsing integration
│   ├── VirusTotalClient.java         # VirusTotal integration
│   └── HeuristicAnalyzer.java        # Local pattern detection
├── DTOs/urlcheck/
│   ├── UrlCheckRequest.java          # Request DTO
│   └── UrlCheckResponse.java         # Response DTO
└── security/
    └── SecurityConfigs.java          # Security configuration
```

### Design Principles

- **Separation of Concerns**: Each detection method is isolated in its own service
- **Dependency Injection**: All components use constructor injection
- **Fail-Safe**: API failures don't crash the system
- **Stateless**: No database, suitable for horizontal scaling
- **Testable**: Services are easily unit testable

## 🧪 Testing

Run unit tests:

```bash
./mvnw test
```

Run specific test class:

```bash
./mvnw test -Dtest=HeuristicAnalyzerTest
```

## 🔐 Security Considerations

### API Keys
- Never commit API keys to version control
- Use environment variables in production:
  ```bash
  export GOOGLE_SAFEBROWSING_API_KEY=your_key
  export VIRUSTOTAL_API_KEY=your_key
  ```
- Or override via command line:
  ```bash
  ./mvnw spring-boot:run -Dspring-boot.run.arguments=--google.safebrowsing.api.key=YOUR_KEY
  ```

### CORS
- Currently configured for `origins = "*"` for development
- In production, restrict to specific origins:
  ```java
  @CrossOrigin(origins = "https://your-mobile-app.com")
  ```

## 🎮 Game Integration

When the API returns `"redirectToGame": true`, the frontend should:
1. Stop loading the URL in WebView
2. Redirect to the awareness game module
3. Display educational content about the detected threat

The backend does NOT render the game - it only signals when to activate it.

## 🚧 Project Scope

### What This Project IS
✅ A URL threat detection API  
✅ A learning project demonstrating Spring Boot best practices  
✅ A hackathon-ready backend component  

### What This Project is NOT
❌ A full antivirus system  
❌ A network-level firewall  
❌ An XDR/EDR solution  
❌ A production-grade enterprise security platform  

## 📝 Development Notes

- This endpoint is **public** and does not require authentication
- Other endpoints in the application still require JWT authentication
- The existing TODO features remain fully functional
- Database is configured but not used by the URL checker

## 🤝 Contributing

This is a learning/hackathon project. Feel free to:
- Add more heuristic detection rules
- Integrate additional threat intelligence APIs
- Improve error handling and logging
- Add more comprehensive tests

## 📄 License

This project is for educational purposes.

## 🙋 Support

For questions about the URL checking feature, please check:
- Google Safe Browsing API documentation: https://developers.google.com/safe-browsing
- VirusTotal API documentation: https://developers.virustotal.com/reference/overview

---

**Built for cybersecurity awareness, not paranoia. Stay informed, stay safe! 🛡️**
