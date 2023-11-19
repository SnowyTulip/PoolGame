package PoolGame.Strategy;

import PoolGame.Game;
import PoolGame.GameObjects.Ball;

public interface IBallPocketStrategy {
    public void fallIntoPocket(Game game, Ball ball);
}
