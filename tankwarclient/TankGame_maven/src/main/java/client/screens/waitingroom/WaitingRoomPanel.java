package client.screens.waitingroom;

import client.model.dto.Game;
import client.services.WaitingRoomService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class WaitingRoomPanel extends JPanel {
    WaitingRoomService waitingRoomService;
    Game currentGame;
    JPanel parentPanel;
    Integer playerId;
    Integer gameId;
    JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    JPanel bodyPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    JPanel playersPanel = new JPanel();
    JPanel gameDetailPanel = new JPanel();
    JLabel waitingRoomTitle = new JLabel();
    JLabel isStartStatusLabel = new JLabel();
    JButton backToLobbyBtn = new JButton("Back to Lobby");
    JButton startGameBtn = new JButton("Start the Game");

    public WaitingRoomPanel(JPanel parentPanel, Integer playerId, Integer gameId) {
        this.waitingRoomService = new WaitingRoomService();
        this.currentGame = waitingRoomService.getGame(gameId);


        this.parentPanel = parentPanel;
        this.playerId = playerId;
        this.gameId = gameId;
        this.setLayout(new GridLayout());

        backToLobbyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) parentPanel.getLayout();
                cardLayout.show(parentPanel, "lobbyPanel");
            }
        });
        startGameBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (waitingRoomService.isStartGame(gameId)) {
                    waitingRoomService.startGame(gameId);
                } else {
                    isStartStatusLabel.setText("You can not start the game!");
                }
            }
        });

        addToMainPanel();
        addToHeaderPanel();
        addToBodyPanel();
        setWaitingRoomTitle();
    }

    private void addToMainPanel() {
        this.setLayout(new GridLayout(2, 1));
        this.add(headerPanel);
        this.add(bodyPanel);
    }

    private void addToBodyPanel() {
        bodyPanel.add(playersPanel);
        bodyPanel.add(Box.createHorizontalStrut(75));
        bodyPanel.add(gameDetailPanel);
        addToPlayersPanel();
        addToGameDetailPanel();
    }

    private void addToGameDetailPanel() {
        gameDetailPanel.setLayout(new BoxLayout(gameDetailPanel, BoxLayout.Y_AXIS));
        gameDetailPanel.add(startGameBtn);
        gameDetailPanel.add(isStartStatusLabel);
    }

    private void addToPlayersPanel() {
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        List<String> players = getUsernames();
        System.out.println(players);
        for (String player : players) {
            JLabel playerLabel = new JLabel(player);
            playerLabel.setBackground(Color.BLUE);
            playerLabel.setOpaque(true);
            playerLabel.setForeground(Color.white);
            playerLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
            playerLabel.setMinimumSize(new Dimension(350, 30));
            playerLabel.setPreferredSize(new Dimension(350, 30));
            playerLabel.setMaximumSize(new Dimension(350, 30));
            playersPanel.add(playerLabel);
            playersPanel.add(Box.createVerticalStrut(5));
        }
    }

    private void addToHeaderPanel() {
        headerPanel.add(backToLobbyBtn);
        headerPanel.add(Box.createHorizontalStrut(100));
        headerPanel.add(waitingRoomTitle);
    }

    private void setWaitingRoomTitle() {
        waitingRoomTitle.setText("Game-" + gameId + " Waiting Room");
        waitingRoomTitle.setFont(new Font("Verdana", Font.PLAIN, 24));
    }

    private List<String> getUsernames() {
        Map<Integer, Integer> players = currentGame.getPlayers();
        List<Integer> playerIds = players.keySet().stream().toList();
        return this.waitingRoomService.getUsernames(playerIds);
    }
}
