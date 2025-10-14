package xyz.zyro.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/user")
public class ApifyController {

	 private static final String APIFY_TOKEN = "apify_api";
	    private static final String ACTOR_ID = "shu8hvrXbJbY3Eb9W";

	    private final RestTemplate restTemplate = new RestTemplate();

	    @GetMapping("/run")
	    public ResponseEntity<?> runActor() {
	        try {
	            // 1️⃣ Start the actor
	            String runUrl = "https://api.apify.com/v2/acts/" + ACTOR_ID + "/runs?token=" + APIFY_TOKEN;

	            ResponseEntity<Map> runResponse = restTemplate.postForEntity(runUrl, null, Map.class);
	            Map<String, Object> data = (Map<String, Object>) runResponse.getBody().get("data");

	            // Get the default dataset ID
	            String defaultDatasetId = (String) data.get("defaultDatasetId");

	            // 2️⃣ Wait for actor to finish (poll until status is "SUCCEEDED")
	            String runId = (String) data.get("id");
	            String runStatusUrl = "https://api.apify.com/v2/actor-runs/" + runId + "?token=" + APIFY_TOKEN;

	            String status;
	            do {
	                Thread.sleep(5000);
	                ResponseEntity<Map> statusResp = restTemplate.getForEntity(runStatusUrl, Map.class);
	                status = (String) ((Map<String, Object>) statusResp.getBody().get("data")).get("status");
	                System.out.println("Actor status: " + status);
	            } while (!status.equals("SUCCEEDED") && !status.equals("FAILED"));

	            // 3️⃣ Fetch dataset results
	            String datasetUrl = "https://api.apify.com/v2/datasets/" + defaultDatasetId + "/items?token=" + APIFY_TOKEN;

	            ResponseEntity<String> datasetResponse = restTemplate.getForEntity(datasetUrl, String.class);

	            return ResponseEntity.ok(datasetResponse.getBody());

	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Error running Apify actor: " + e.getMessage());
	        }
	    }
}
