import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NgrokURL {
    static String getTunnelUrl() {
        try {
            // configure a request to ngrok api
            var request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.ngrok.com/tunnels"))
                    .header("Authorization", "Bearer 2K8b1iawfqvJEWz8UhK7i9UOzml_36h3tWg7TzPSJbyRQSwpQ")
                    .header("Ngrok-Version", "2")
                    .GET()
                    .build();

            var client = HttpClient.newHttpClient();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            // get a first tunnel from the response json
            JsonNode tunnel = mapper.readTree(response.body()).get("tunnels").get(0);

            if (tunnel == null) {
                throw new IllegalStateException("No accessible tunnels");
            }

            return tunnel.get("public_url").asText();
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException("cannot send a request or map a Json Object", e);
        }
    }
}
