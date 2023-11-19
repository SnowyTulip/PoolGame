package PoolGame.Strategy;

import PoolGame.Game;
import PoolGame.GameObjects.Ball;

public class GameStrategyContext {
    private IBallPocketStrategy strategy;
    public void setStrategy(IBallPocketStrategy strategy){
        this.strategy = strategy;
    }
    public void executeStrategy(Game game, Ball ball){
        if(this.strategy != null)
            strategy.fallIntoPocket(game, ball);
    }
}
