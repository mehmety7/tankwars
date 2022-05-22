package server.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import server.dao.InMemoryDao;
import server.model.dto.Game;
import server.model.dto.Tank;
import server.model.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@RequiredArgsConstructor
public class GameService {

    private static final Integer INITIAL_SCORE_POINT = 0;

    private InMemoryDao inMemoryDao = InMemoryDao.getInstance();
    private PlayerService playerService = PlayerService.getInstance();
    private TankService tankService = new TankService();

    public Game createGame(Game game) {
        Player player = playerService.getPlayer(game.getId());

        Map<Player, Integer> map = new HashMap<>();
        map.put(player, INITIAL_SCORE_POINT);

        game.setPlayers(map);
        game.setIsStarted(Boolean.FALSE);

        inMemoryDao.games.put(game.getId(), game);

        return game;
    }

    public Game joinGame(Integer gameId, Integer playerId) {
        Player player = playerService.getPlayer(playerId);
        inMemoryDao.games.get(gameId).getPlayers().put(player, INITIAL_SCORE_POINT);
        return inMemoryDao.games.get(gameId);
    }

    public List<Tank> startGame(Integer gameId) {
        Game game = inMemoryDao.games.get(gameId);
        game.setIsStarted(Boolean.TRUE);

        if (game.getPlayers().size() < 2) {
            return null;
        }

        return tankService.createTanksForNewGame(gameId);
    }

    public Game getGame(Integer gameId) {
        return inMemoryDao.games.get(gameId);
    }

    public List<Game> getAllGames() {
        return new ArrayList<>(inMemoryDao.games.values());
    }

    public List<Game> getLobbies() {
        Map<Integer, Game> allGames = inMemoryDao.games;
        Map<Integer, Game> lobbies = new HashMap<>();
        for (Game game : allGames.values()) {
            if (Boolean.FALSE.equals(game.getIsStarted())) {
                lobbies.put(game.getId(), game);
            }
        }

        return new ArrayList<>(lobbies.values());
    }

    public Boolean deleteGame(Integer gameId) {
        inMemoryDao.games.remove(gameId);
        List<Tank> tanksInGame = tankService.getTanksInGame(gameId);
        for (Tank tank : tanksInGame) {
            inMemoryDao.tanks.remove(tank.getPlayerId());
        }
        return Boolean.TRUE;
    }

    public Boolean updatePlayerPoint(Integer gameId, Integer playerId, Integer point) {
        Player player = playerService.getPlayer(playerId);
        inMemoryDao.games.get(gameId).getPlayers().put(player, point);
        return Boolean.TRUE;
    }

}
