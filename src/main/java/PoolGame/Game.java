package PoolGame;

import PoolGame.GameObjects.Ball;
import PoolGame.GameObjects.Balls;
import PoolGame.GameObjects.Table;
import PoolGame.IObject.Drawable;
import PoolGame.IObject.GameColor;
import PoolGame.IObject.Movable;
import PoolGame.Strategy.*;
import PoolGame.configBuildFactory.BallFactory;
import PoolGame.configBuildFactory.BaseConfigFactory;
import PoolGame.configBuildFactory.TableFactory;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Game 实例
 */
public class Game {


    enum GameStatus{
        start,reset, @SuppressWarnings("unused") success, @SuppressWarnings("unused") fail
    }
    private GameStatus status;
    private final Balls balls;
    private final Table table;
    private final Canvas canvas;
    // 策略模式上下文
    private final GameStrategyContext fGameStrategyContext;
    private final Drawable rootDrawables = new DummyDrawable();
    // 所有球
    private List<Movable> movables;
    /* 待删除列表 ballsToRemove
     * 因为我们无法在遍历数组的时候删除球,我们允许它跑完当次循环
     * 在循环时将需要删除的球添加到ballsToRemove中
     * 循环结束后我们将需要删除的球删除
     * @see #scheduleToRemove(Ball ball)
    */

    private List<Ball> ballsToRemove;

    public Game(Canvas canvas){
        BaseConfigFactory tableFactory = new TableFactory();
        BaseConfigFactory ballsFactory = new BallFactory();
        table = (Table)tableFactory.CreateGameObject();
        balls = (Balls) ballsFactory.CreateGameObject();
        this.canvas = canvas;
        movables = balls.getBalls();
        ballsToRemove = new ArrayList<>();
        status = GameStatus.start;
        this.canvas.setFocusTraversable(true);
        this.canvas.requestFocus();
        //在此处添加了功能,点击“Ctrl+F” 也可以重置游戏
        this.canvas.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.F) {
                this.status = GameStatus.reset;
            }
        });
        fGameStrategyContext = new GameStrategyContext();
    }

    private void resetGame() {
        movables = balls.getBalls();
        for (Movable movable : movables) {
            ((Ball) movable).reset();
        }
    }
    public void gameOver(){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("游戏失败");
            alert.setHeaderText(null);
            alert.setContentText("很遗憾，您失败了！");

            ButtonType resetButton = new ButtonType("重新开始");
            alert.getButtonTypes().setAll(resetButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == resetButton) {
                resetGame();
            }
        });

    }
    public void gameSuccess(){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("游戏成功");
            alert.setHeaderText(null);
            alert.setContentText("恭喜您 , 您很厉害！");

            ButtonType resetButton = new ButtonType("重新开始");
            alert.getButtonTypes().setAll(resetButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == resetButton) {
                resetGame();
            }
        });

    }

    public void addDrawables(Group root) {
        ObservableList<Node> groupChildren = root.getChildren();
        table.addToGroup(groupChildren);
        balls.addToGroup(groupChildren);
        rootDrawables.addToGroup(groupChildren);

    }

    /**
     * @param ball 需要移除的球
     */
    public void scheduleToRemove(Ball ball){
        this.ballsToRemove.add(ball);
    }

    /**
     * 循环结束后将球移除,实际上需要移除this.movables中的ball
     */
    public void removeBalls(){
        this.movables.removeAll(this.ballsToRemove);
        this.ballsToRemove.clear();
    }
    private void processBallFallIntoPocket(Ball ball){
        GameColor Color = ball.getColor();
        IBallPocketStrategy strategy;
        switch (Color){
            case red   -> strategy = new RedBallFallIntoPocket();
            case blue  -> strategy = new BlueBallFallIntoPocket();
            case white -> strategy = new WhiteBallFallIntoPocket();
            default    -> strategy = null;
        }
        fGameStrategyContext.setStrategy(strategy);
        fGameStrategyContext.executeStrategy(this,ball);
        //判断完成进球后需要判断一下是否该游戏结束了
        //只要目前桌子上仅有一个球，并且他是白球，直接游戏成功
        List<Movable> TempBalls = new ArrayList<>(this.movables);
        TempBalls.removeAll(this.ballsToRemove);
        if(TempBalls.size() == 1 &&
                ((Ball) TempBalls.get(0)).getColor() == GameColor.white ){
            strategy = new LastBallFallIntoPocket();
            fGameStrategyContext.setStrategy(strategy);
            fGameStrategyContext.executeStrategy(this,ball);
        }
    }
    // tick() is called every frame, handle main game logic here
    public void tick() {
        if(status == GameStatus.start) {
            //先移动球        move()
            //根据摩擦力减速球 slowDownForFriction()
            //处理碰撞        handleCollision()
            //判断是否进球     DoPocketBehave()
            //如果进球使用策略  processBallFallIntoPocket()
            for (Movable movable : movables) {
                movable.move();
                movable.slowDownForFriction(table.getFriction());
                movable.handleCollision(movables, table.getBounds());
                Boolean isFallIntoPocket = movable.DoPocketBehave(table.getPockets());
                if (isFallIntoPocket) processBallFallIntoPocket((Ball) movable);
            }
            //移除球
            removeBalls();
        } else if (status == GameStatus.reset) {
            resetGame();
            status = GameStatus.start;
        }
    }

}


class DummyDrawable implements Drawable {

    @Override
    public Node getNode() {
        return null;
    }

    @Override
    public void addToGroup(ObservableList<Node> group) {}
}
