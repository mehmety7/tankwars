package test;

import server.model.entity.Player;
import server.utilization.JsonUtil;

import java.io.*;
import java.net.*;

class TestClient {

    public static void main(String[] args)
    {

        try (Socket socket = new Socket("localhost", 12345)) {

            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Player player = Player.builder().id(5).username("user").password("password").build();
            String data = JsonUtil.toJson(player);
            String request = "LG" + "OK" + data;
            System.out.println(request);

            while (!"exit".equalsIgnoreCase(request)) {

                out.println(request);
                out.flush();

                System.out.println("Server replied " + in.readLine());
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}