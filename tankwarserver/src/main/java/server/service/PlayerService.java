package server.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import server.dao.PlayerDao;
import server.model.entity.Player;
import server.utilization.HashUtil;

import java.util.List;
import java.util.Objects;

@Setter
@RequiredArgsConstructor
public class PlayerService {

    private static final Integer CREATE_ERROR_RETURN_VALUE = -1;

    private static PlayerService playerService;

    private PlayerDao playerDao = PlayerDao.getInstance();

    public static PlayerService getInstance() {
        if (Objects.isNull(playerService)) {
            playerService = new PlayerService();
        }
        return playerService;
    }

    public Player createPlayer(Player player) {
        player.setPassword(HashUtil.hashValue(player.getPassword()));
        Integer newPlayerId = playerDao.createPlayer(player);
        if (newPlayerId.equals(CREATE_ERROR_RETURN_VALUE)) {
            System.out.println("Create player operation is failed");
            return null;
        }
        player.setId(newPlayerId);
        return player;
    }

    public Player getPlayer(Integer playerId) {
        return playerDao.getPlayer(playerId);
    }

    public Player getPlayer(String username) {
        return playerDao.getPlayer(username);
    }

    public List<Player> getActivePlayers() {
        return playerDao.getActivePlayers();
    }

    public boolean deletePlayer(Integer playerId) {
        return playerDao.deletePlayer(playerId);
    }

    public boolean updatePlayerActivate(Integer playerId) {
        return playerDao.updateActive(playerId);
    }

    public Player login (Player player) {
        Player result = playerDao.getPlayer(player.getUsername());
        if (Objects.nonNull(result)) {
            if (result.getPassword().equals(HashUtil.hashValue(player.getPassword()))) {
                return result;
            }
        }
        return null;
    }



}
