package PoolGame.Strategy;

import PoolGame.Game;
import PoolGame.GameObjects.Ball;

public class LastBallFallIntoPocket implements IBallPocketStrategy{
    @Override
    public void fallIntoPocket(Game game, Ball ball) {
//        GameColor Color = ball.getColor();
//        if(Color == GameColor.white){
//
//        }
        game.gameSuccess();
    }
}
