package client.game;

import client.model.dto.Tank;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class EnemyPlayer {
    public Tank tank;
    private Integer speed = 2;
    private BufferedImage up, down, left, right;
    private GamePanel gp;

    public EnemyPlayer(GamePanel gp, Tank tank) {
        this.gp = gp;
        this.tank = tank;
        getPlayerImage();
    }

    public void getPlayerImage() {
        try {
            up = ImageIO.read(new File(readFileFromResourcesAsString("red_up.png")));
            down = ImageIO.read(new File(readFileFromResourcesAsString("red_down.png")));
            left = ImageIO.read(new File(readFileFromResourcesAsString("red_left.png")));
            right = ImageIO.read(new File(readFileFromResourcesAsString("red_right.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
//        x+=speed;
//        tank.setPositionX(tank.getPositionX()+1);

//        if(keyHandler.upPressed == true){
//            direction = "up";
//            y -= speed;
//        }else if(keyHandler.downPressed == true){
//            direction = "down";
//            y += speed;
//        }else if(keyHandler.leftPressed == true){
//            direction = "left";
//            x -= speed;
//        }else if(keyHandler.rightPressed == true){
//            direction = "right";
//            x += speed;
//        }
    }

    public void draw(Graphics2D g2) {
//        g2.setColor(Color.white);
//        g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        BufferedImage image = null;

        switch (tank.getFaceOrientation()) {
            case UP:
                image = up;
                break;
            case DOWN:
                image = down;
                break;
            case LEFT:
                image = left;
                break;
            case RIGHT:
                image = right;
                break;
        }

        g2.drawImage(image, tank.getPositionX(), tank.getPositionY(), gp.tankSize, gp.tankSize, null);


        g2.setColor(Color.YELLOW);
        int hp;
         hp = tank.getHealth()/10;

        String heathBar = "[" + Integer.toString(hp) + "]";
        g2.drawString(heathBar, tank.getPositionX()+20, tank.getPositionY()+60);
        g2.setColor(Color.GREEN);


    }

    private String readFileFromResourcesAsString(String fileName) throws IOException {
        Path filePath = Paths.get("./src/main/resources/enemy/" + fileName);
        return filePath.toString();
    }

}
