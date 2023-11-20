package PoolGame.Strategy;

import PoolGame.Game;
import PoolGame.GameObjects.Ball;

/**
 * 策略
 * 最后一个非白色球落入 -> 游戏成功
 */
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
