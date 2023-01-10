import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.User;

import java.util.Map;
import java.util.Scanner;

public class Client {
    private static User user;

    public static void main(String[] args) {
        var url = NgrokURL.getTunnelUrl();
        System.out.println(url);
        RequestHandler handler = new RequestHandler(url);
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter your name");
        String name = sc.nextLine();

        var req = handler.getPresetRequest("/chat_api/v1/user", Map.of("name", name))
                .GET().
                build();

        var resp = handler.sendRequestAndGetResponse(req);
        ObjectMapper mapper = new ObjectMapper();
        try {
            user = mapper.readValue(resp, User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(user.id);
    }
}
