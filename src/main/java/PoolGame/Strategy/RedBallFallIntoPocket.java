package PoolGame.Strategy;

import PoolGame.Game;
import PoolGame.GameObjects.Ball;

/**
 * 策略
 * 红球进入 -> 移除该球
 */
public class RedBallFallIntoPocket implements IBallPocketStrategy{
    @Override
    public void fallIntoPocket(Game game, Ball ball) {
        ball.setEnable(false);
        game.scheduleToRemove(ball);
    }
}
