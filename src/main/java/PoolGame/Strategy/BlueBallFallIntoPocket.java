package PoolGame.Strategy;

import PoolGame.Game;
import PoolGame.GameObjects.Ball;

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
