package PoolGame.Strategy;

import PoolGame.Game;
import PoolGame.GameObjects.Ball;

/**
 * 策略
 * 蓝色球进入后如果球是第一次进入 -> 球复位
 * 第二次进入 -> 设置球不可见 -> 移除球
 */
public class BlueBallFallIntoPocket implements IBallPocketStrategy{
    @Override
    public void fallIntoPocket(Game game, Ball ball) {
        int CountOfFallIntoPocket = ball.getCountOfFallIntoPocket();
        if(CountOfFallIntoPocket == 1){
            ball.goBackToInitialPosition();
        }
        else if(CountOfFallIntoPocket >= 2){
            ball.setEnable(false);
            game.scheduleToRemove(ball);
        }
    }
}
