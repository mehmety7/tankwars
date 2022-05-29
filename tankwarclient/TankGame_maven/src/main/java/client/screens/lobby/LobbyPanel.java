package client.screens.lobby;

import client.model.dto.Game;
import client.model.dto.Message;
import client.model.entity.Player;
import client.model.request.PlayerGameRequest;
import client.screens.leadership.LeadershipPanel;
import client.screens.waitingroom.WaitingRoomPanel;
import client.services.SingletonSocketService;
import client.socket.ClientSocket;
import client.util.JsonUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import java.util.stream.Collectors;

public class LobbyPanel extends JPanel {
    JPanel parentPanel = new JPanel();

    //Panels
    JPanel buttonsPanel = new JPanel();
    JPanel gameRooms = new JPanel();
    JPanel chat = new JPanel(new GridLayout(0,1));

    //Timer
    Timer t = new Timer();

    //Buttons
    JButton newGameButton = new JButton("New Game");
    JButton leadershipButton = new JButton("LeaderShip");
    JButton aboutUsButton = new JButton("About Us");
    JButton logoutButton = new JButton("Log Out");
    JButton refreshButton = new JButton("Refresh");


    //Labels
    JLabel gameTitleLabel = new JLabel("TankWars Games");
    JLabel activePlayersLabel = new JLabel("Active Players");
    JPanel activePlayerPanel = new JPanel();

    public LobbyPanel(JPanel parentPanel, Integer playerId) {
        this.parentPanel = parentPanel;
        GridLayout gridLayout = new GridLayout(4,2);
        setLayout(null);
        gridLayout.setHgap(20);
        gridLayout.setVgap(10);

        String columnName = "Player Username";

        //open New Game
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Lobby ekranına gelen player datası içindeki playerIdyi newGamePanelına ilet
                NewGamePanel newGamePanel = new NewGamePanel(parentPanel, playerId);
                parentPanel.add(newGamePanel, "newGamePanel");

                CardLayout cardLayout = (CardLayout) parentPanel.getLayout();
                cardLayout.show(parentPanel, "newGamePanel");
            }
        });

        //open Leadership
        leadershipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //leadership panel
                LeadershipPanel leadershipPanel = new LeadershipPanel(parentPanel);
                parentPanel.add(leadershipPanel, "leadershipPanel");
                CardLayout cardLayout = (CardLayout) parentPanel.getLayout();
                cardLayout.show(parentPanel, "leadershipPanel");
            }
        });

        //open About Us
        aboutUsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cardLayout = (CardLayout) parentPanel.getLayout();
                cardLayout.show(parentPanel, "aboutUsPanel");
            }
        });

        //LogOut from game
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO perform LogOut
                ClientSocket cs = SingletonSocketService.getInstance().clientSocket;
                PlayerGameRequest request = PlayerGameRequest.builder().playerId(playerId).build();
                cs.sendMessage("LT", request);

                if(cs.response().startsWith("OK")){
                    CardLayout cardLayout = (CardLayout) parentPanel.getLayout();
                    cardLayout.show(parentPanel, "loginPanel");
                } else {
                    System.out.println("LogOut response hatası");
                    JOptionPane.showMessageDialog(parentPanel,"LogOut Error");
                }

            }
        });

        //Refresh Lobby
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ClientSocket cs = SingletonSocketService.getInstance().clientSocket;
                cs.sendMessage("GU", null);
                System.out.println("SERVER RESPONSE (GU) " + cs.response());

                //get games
                if(cs.response().startsWith("OK")){
                    String gamesDataString = cs.response().substring(2);
                    gameRooms.removeAll();
                    List <Game> games =  JsonUtil.fromListJson(gamesDataString, Game[].class);
                    System.out.println("games\n" + games);
                    System.out.println();

                    for (Game game : games) {
                        String gameInfoString = String.format("Tour:%d Speed:%.1f Map:%s", game.getTourNumber(),
                                game.getShootingSpeed(), game.getMapType());
                        Integer gameId = game.getId();
                        JLabel gameInfo = new JLabel(gameInfoString);
                        gameInfo.setSize(new Dimension(370,50));
                        gameInfo.setBackground(Color.red);

                        //join button
                        JButton joinButton = new JButton("JOİN"+gameId);
                        joinButton.setSize(new Dimension(80,50));
                        joinButton.setBackground(new Color(0,118,255));
                        joinButton.setForeground(Color.WHITE);

                        JPanel gameComponent = new JPanel(new FlowLayout());
                        gameComponent.setSize(new Dimension(450,50));
                        gameComponent.setBackground(Color.red);
                        gameComponent.add(gameInfo);
                        gameComponent.add(joinButton);
                        joinButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                PlayerGameRequest playerGameRequest = PlayerGameRequest.builder().gameId(gameId).playerId(playerId).build();
                                cs.sendMessage("JG", playerGameRequest);
                                System.out.println(gameId);
                                WaitingRoomPanel waitingRoomPanel = new WaitingRoomPanel(parentPanel, playerId, gameId);
                                parentPanel.add(waitingRoomPanel, "waitingRoomPanel");

                                CardLayout cardLayout = (CardLayout) parentPanel.getLayout();
                                cardLayout.show(parentPanel, "waitingRoomPanel");
                            }
                        });

                        gameRooms.add(gameComponent);
                    }

                } else {
                    System.out.println("No available game or response error");
                    // JOptionPane.showMessageDialog(parentPanel, "No game or Response error");
                }

                //get messages
                cs.sendMessage("GM",null);
                if(cs.response().contains("OK")){
                    String messagesDataString = cs.response().substring(2);

                    List <Message> messages =  JsonUtil.fromListJson(messagesDataString, Message[].class);
                    System.out.println("games\n" + messages);
                    System.out.println();
                }else {
                    System.out.println("No available message or response error");
                    // JOptionPane.showMessageDialog(parentPanel, "No message or Response error");
                }

                //get active players
                cs.sendMessage("AG",null);
                if(cs.response().contains("OK")){
                    String gamesDataString = cs.response().substring(2);
                    List <Player> activePlayersList =  JsonUtil.fromListJson(gamesDataString, Player[].class);
                    List<String> activeData = new ArrayList<>();
                    activePlayerPanel.removeAll();
                    activePlayerPanel.setLayout(new BoxLayout(activePlayerPanel,BoxLayout.Y_AXIS));

                    for(Player player: activePlayersList){
                        activeData.add(player.getUsername());

                    }
                    String[] tmp = activeData.stream().toArray(String[]::new);
                    DefaultTableModel model = new DefaultTableModel();
                    model.addColumn(columnName, tmp);
                    JTable activePlayers = new JTable(model);
                    activePlayerPanel.setBounds(475, 120, 200, 250);
                    activePlayerPanel.setBackground(Color.yellow);
                    activePlayers.setEnabled(false);
                    activePlayers.getTableHeader().setReorderingAllowed(false);

                    JScrollPane scrollPane = new JScrollPane(activePlayers);
                    activePlayerPanel.add(activePlayersLabel);
                    activePlayerPanel.add(scrollPane);
                    System.out.println(activeData);
                }else {
                    System.out.println("No available active players or error");
                    // JOptionPane.showMessageDialog(parentPanel, "No available active players or Response error");
                }
                activePlayerPanel.updateUI();
                gameRooms.updateUI();

            };



        });

        gameRooms.setBounds(15,110,450,520);
        gameRooms.setBackground(new Color(216,229,241));

        chat.setBackground(new Color(199,186,217));
        chat.setBounds(475,380,450,220);

        JTextField chatInput = new JTextField("Type Message");
        chatInput.setBounds(475,600,450,30);
        chatInput.setBackground(new Color(226,221,235));
        chatInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ClientSocket cs = SingletonSocketService.getInstance().clientSocket;
                    Message message = Message.builder().playerId(playerId).playerUserName("Burak").text(chatInput.getText()).build();
                    cs.sendMessage("CM",message);
                    System.out.println("CHAT INPUT "+chatInput.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        //Customize Buttons
        newGameButton.setBackground(new Color(0,118,255));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFocusable(false);

        leadershipButton.setBackground(new Color(0,118,255));
        leadershipButton.setForeground(Color.WHITE);
        leadershipButton.setFocusable(false);

        aboutUsButton.setBackground(new Color(0,118,255));
        aboutUsButton.setForeground(Color.WHITE);
        aboutUsButton.setFocusable(false);

        logoutButton.setBackground(new Color(0,118,255));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusable(false);

        refreshButton.setBackground(new Color(0,118,255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusable(false);

        gameTitleLabel.setFont(new Font("Verdana", Font.PLAIN,24));
        gameTitleLabel.setBounds(10,10,400,75);

        //Add buttons to the buttonsPanel
        buttonsPanel.add(newGameButton);
        buttonsPanel.add(leadershipButton);
        buttonsPanel.add(aboutUsButton);
        buttonsPanel.add(logoutButton);
        buttonsPanel.add(refreshButton);
        buttonsPanel.setBounds(350,10,700,100);


        this.add(gameTitleLabel);
        this.add(buttonsPanel);
        this.add(gameRooms);
        this.add(chat);
        this.add(chatInput);
        this.add(activePlayerPanel);
    }
}