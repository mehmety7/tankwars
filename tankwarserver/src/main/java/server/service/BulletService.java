package server.service;

import lombok.NoArgsConstructor;
import server.constants.ConstantsForInnerLogic;
import server.dao.InMemoryDao;
import server.model.dto.Bullet;
import server.model.dto.Game;
import server.model.dto.Tank;
import server.utilization.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class BulletService {

    private static BulletService bulletService;

    public static BulletService getInstance() {
        if (Objects.isNull(bulletService)) {
            bulletService = new BulletService();
        }

        return bulletService;
    }

    private final InMemoryDao inMemoryDao = InMemoryDao.getInstance();
    private final TankService tankService = TankService.getInstance();

    public Bullet createOrUpdateBullet(Bullet bullet){
        inMemoryDao.bullets.removeIf(bulletPair -> bulletPair.getSecond().getTankId().equals(bullet.getTankId()) && bulletPair.getSecond().getBulletId().equals(bullet.getBulletId()));
        inMemoryDao.bullets.add(new Pair<>(bullet.getTankId(), bullet));
        return bullet;
    }

    public void removeBullets(Integer playerId){
        inMemoryDao.bullets.removeIf(bulletPair -> bulletPair.getFirst().equals(playerId));
    }

    private boolean isTankGotHit(Bullet bullet, Tank tank){
        //Tankın x ve y noktaları tankın namlusunun x ve y koordinatlarını temsil ediyor.
        //Buradaki hesapta birden fazla tankı aynı anda vurmak mümkün, baya kolay bir hesap :D
        switch (bullet.getFaceOrientation()){
            case UP:
                if ((tank.getPositionX() + ConstantsForInnerLogic.tankSize / 2 >= bullet.getPositionX() && tank.getPositionX() - ConstantsForInnerLogic.tankSize / 2 <= bullet.getPositionX() + 1) && tank.getPositionY() <= bullet.getPositionY()) {
                    return true;
                }
                break;
            case DOWN:
                if ((tank.getPositionX() + ConstantsForInnerLogic.tankSize / 2 >= bullet.getPositionX() && tank.getPositionX() - ConstantsForInnerLogic.tankSize / 2 <= bullet.getPositionX() + 1) && tank.getPositionY() >= bullet.getPositionY()) {
                    return true;
                }
                break;
            case LEFT:
                if ((tank.getPositionY() + ConstantsForInnerLogic.tankSize / 2 >= bullet.getPositionY() && tank.getPositionY() - ConstantsForInnerLogic.tankSize / 2 <= bullet.getPositionY() + 1) && tank.getPositionX() <= bullet.getPositionX()) {
                    return true;
                }
                break;
            case RIGHT:
                if ((tank.getPositionY() + ConstantsForInnerLogic.tankSize / 2 >= bullet.getPositionY() && tank.getPositionY() - ConstantsForInnerLogic.tankSize / 2 <= bullet.getPositionY() + 1) && tank.getPositionX() >= bullet.getPositionX()) {
                    return true;
                }
                break;
        }
        return false;
    }

    public List<Tank> tanksThatGotHit(Game game, Bullet bullet){
        List<Tank> hittedTankList = new ArrayList<>();
        List<Tank> allTanks = tankService.getTanksInGame(game.getId());
        allTanks.forEach((tank -> {
            if(isTankGotHit(bullet, tank)){
                hittedTankList.add(tank);
            }
        }));
        return hittedTankList;
    }
}
