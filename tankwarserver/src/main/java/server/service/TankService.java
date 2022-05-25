package server.service;

import lombok.RequiredArgsConstructor;
import server.bean.BeanHandler;
import server.constants.ConstantsForInnerLogic;
import server.dao.InMemoryDao;
import server.model.dto.Bullet;
import server.model.dto.Game;
import server.model.dto.Tank;
import server.model.entity.Player;
import server.model.enumerated.FaceOrientation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class TankService {

    public static TankService tankService;

    public static TankService getInstance() {
        if (Objects.isNull(tankService)) {
            tankService = new TankService(BeanHandler.inMemoryDao, BeanHandler.bulletService, BeanHandler.playerService);
        }
        return tankService;
    }

    private final InMemoryDao inMemoryDao;
    private final BulletService bulletService;
    private final PlayerService playerService;


    public List<Tank> createTanksForNewGame(Integer gameId) {
        Game game = inMemoryDao.games.get(gameId);
        Integer iterator = 1;

        List<Player> players = new ArrayList<>();

        for (Integer playerId : game.getPlayers().keySet()) {
            players.add(playerService.getPlayer(playerId));
        }

        for (Player player : players) {
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
            createOrUpdateTank(tank);
        }
        return getTanksInGame(gameId);
    }

    private void createOrUpdateTank(Tank tank) {
        inMemoryDao.tanks.put(tank.getPlayerId(), tank);
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
        bulletService.removeBullets(playerId);
        return inMemoryDao.tanks.remove(playerId);
    }
    /* Tank ateş ettiğinde onun önünde bir mermi oluşturulacak.
    *  Şimdilik merminin konumunu tankın konumu ile aynı yaptım.
    *  İlerde bu değiştirilmeli
    *  Bu arada fonksiyon için daha yaratıcı bir isim bekliyorum.
    *  Service katmanında shoot demek bana garip geldi :D */
    public Tank createBullet(Tank tank){
        //TODO: Merminin konumunu tankın biraz ilerisinde olacak şekilde ayarla
        int offsetX = 0;
        int offsetY = 0;
        switch (tank.getFaceOrientation()){
            case LEFT:
                offsetY = 0;
                offsetX = -1 * ConstantsForInnerLogic.bulletOffset;
                break;
            case RIGHT:
                offsetY = 0;
                offsetX = ConstantsForInnerLogic.bulletOffset;
                break;
            case UP:
                offsetX = 0;
                offsetY = -1 * ConstantsForInnerLogic.bulletOffset;
                break;
            case DOWN:
                offsetX = 0;
                offsetY = ConstantsForInnerLogic.bulletOffset;
                break;
        }
        Bullet bullet = Bullet.builder()
                .tankId(tank.getPlayerId())
                .bulletId(UUID.randomUUID())
                .positionX(inMemoryDao.tanks.get(tank.getPlayerId()).getPositionX() + offsetX)
                .positionY(inMemoryDao.tanks.get(tank.getPlayerId()).getPositionY() + offsetY)
                .faceOrientation(inMemoryDao.tanks.get(tank.getPlayerId()).getFaceOrientation())
                .build();
        bulletService.createOrUpdateBullet(bullet);
        return tank;
    }
}
