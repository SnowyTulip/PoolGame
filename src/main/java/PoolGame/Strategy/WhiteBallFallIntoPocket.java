package PoolGame.Strategy;

import PoolGame.Game;
import PoolGame.GameObjects.Ball;

/**
 * 白球进洞 -> 移除球 -> 游戏失败
 */
public class WhiteBallFallIntoPocket implements IBallPocketStrategy{
    @Override
    public void fallIntoPocket(Game game, Ball ball) {
        ball.setEnable(false);
        game.scheduleToRemove(ball);
        game.gameOver();
    }
}
