package server.service;

import server.dao.InMemoryDao;
import server.model.dto.Game;
import server.model.dto.Tank;
import server.model.entity.Player;
import server.model.enumerated.FaceOrientation;

import java.util.ArrayList;
import java.util.List;

public class TankService {

    private InMemoryDao inMemoryDao = InMemoryDao.getInstance();

    public Tank createOrUpdateTank(Tank tank) {
        inMemoryDao.tanks.put(tank.getPlayerId(), tank);
        return tank;
    }

    public List<Tank> createTanksForNewGame(Integer gameId) {
        Game game = inMemoryDao.games.get(gameId);
        Integer iterator = 1;
        for (Player player : game.getPlayers().keySet()) {
            FaceOrientation faceOrientation;
            Integer newX;
            Integer newY;
            if (iterator.equals(1)) {
                faceOrientation = FaceOrientation.UP;
                newX = 0;
                newY = 0;
            } else if (iterator.equals(2)) {
                faceOrientation = FaceOrientation.DOWN;
                newX = 0;
                newY = 100;
            } else if (iterator.equals(3)) {
                faceOrientation = FaceOrientation.DOWN;
                newX = 100;
                newY = 100;
            } else {
                faceOrientation = FaceOrientation.UP;
                newX = 100;
                newY = 0;
            }
            Integer health = 100;
            Tank tank = Tank.builder()
                    .playerId(player.getId())
                    .gameId(gameId)
                    .faceOrientation(faceOrientation)
                    .health(health)
                    .positionX(newX)
                    .positionY(newY)
                    .build();
            inMemoryDao.tanks.put(player.getId(), tank);
        }
        return getTanksInGame(gameId);
    }

    public Tank getTank(Integer playerId) {
        return inMemoryDao.tanks.get(playerId);
    }

    public List<Tank> getTanksInGame(Integer gameId) {
        List<Tank> tanks = new ArrayList<>();
        for (Tank tank : inMemoryDao.tanks.values()) {
            if (tank.getGameId().equals(gameId)) {
                tanks.add(tank);
            }
        }
        return tanks;
    }

    public Tank deleteTank(Integer playerId) {
        return inMemoryDao.tanks.remove(playerId);
    }

}
