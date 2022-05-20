import client.game.GamePanel;
import client.model.dto.Tank;
import client.model.entity.Player;
import client.model.enumerated.FaceOrientation;
import client.socket.ClientSocket;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Tank Wars");
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Tank Game");


        //todo  ====================================


        List<Tank> tanks = new ArrayList<Tank>();

        tanks.add(Tank.builder()
                .playerId(1)
                .gameId(1)
                .faceOrientation(FaceOrientation.UP)
                .health(10)
                .positionX(100)
                .positionY(300)
                .build());

        tanks.add(Tank.builder()
                .playerId(2)
                .gameId(1)
                .faceOrientation(FaceOrientation.DOWN)
                .health(10)
                .positionX(300)
                .positionY(300)
                .build());

        tanks.add(Tank.builder()
                .playerId(3)
                .gameId(1)
                .faceOrientation(FaceOrientation.DOWN)
                .health(10)
                .positionX(400)
                .positionY(300)
                .build());

        GamePanel gamePanel = new GamePanel(1, tanks);


        //todo  ====================================


        window.add(gamePanel);

        window.pack(); //fit

        window.setLocationRelativeTo(null);
        window.setVisible(true);
        ClientSocket cs = new ClientSocket("localhost", 12345);
        Player player = Player.builder().username("user").password("password").build();
        cs.sendMessage("LG", player);
        System.out.println(cs.response());
    }
}
