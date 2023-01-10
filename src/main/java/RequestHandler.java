import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;

public class RequestHandler {
    private final String url;
    private final HttpClient client;

    public RequestHandler(String url) {
        this.url = url;
        client = HttpClient.newHttpClient();
    }

    HttpRequest.Builder getPresetRequest(String endPoint, Map<String, String> params) {
        String uriStr = url + endPoint;
        if (!params.isEmpty())
            uriStr += "?";

        var paramValues = new ArrayList<String>();
        params.forEach((k, v) -> {
            paramValues.add(k + "=" + v);
        });
        uriStr += String.join("&", paramValues);

        return HttpRequest.newBuilder()
                .uri(URI.create(uriStr))
                .header("Content-Type", "application/json")
                .header("ngrok-skip-browser-warning", "skip");
    }

    String sendRequestAndGetResponse(HttpRequest request) {
        try {
            var resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            return resp.body();
        }
        catch (InterruptedException | IOException exc) {
            throw new IllegalStateException("Something went wrong during sending request", exc);
        }
    }
}
