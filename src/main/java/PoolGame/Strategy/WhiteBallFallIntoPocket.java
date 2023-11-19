package PoolGame.Strategy;

import PoolGame.Game;
import PoolGame.GameObjects.Ball;

public class WhiteBallFallIntoPocket implements IBallPocketStrategy{
    @Override
    public void fallIntoPocket(Game game, Ball ball) {
        ball.setEnable(false);
        game.scheduleToRemove(ball);
        game.gameOver();
    }
}
