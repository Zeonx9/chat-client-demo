import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import entities.Chat;
import entities.User;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        // get the url of the tunnel
        var url = NgrokURL.getTunnelUrl();
        System.out.println(url);

        // initialize needed objects
        RequestHandler handler = new RequestHandler(url);
        Scanner sc = new Scanner(System.in);
        ObjectMapper mapper = new ObjectMapper();

        // ask for a name
        System.out.println("Enter your name");
        String name = sc.nextLine();

        // this requests just searches the name on server and returns a User object
        var req = handler.getPresetRequest("/chat_api/v1/user", Map.of("name", name))
                .GET().
                build();
        var resp = handler.sendRequestAndGetResponse(req);

        // interpret json object
        User user;
        try {
            user = mapper.readValue(resp, User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Your ID is: " + user.id);


        // another request to display the chats of a user
        req = handler.getPresetRequest("/chat_api/v1/users/" + user.id + "/chats", Map.of())
                .GET()
                .build();
        resp = handler.sendRequestAndGetResponse(req);

        // interpret the result and show
        List<Chat> chats;
        try {
            chats = mapper.readValue(resp, new TypeReference<>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Your Chats:");
        chats.forEach(System.out::println);
    }
}
