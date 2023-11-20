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


public class Game {


    enum GameStatus{
        start,reset, @SuppressWarnings("unused") success, @SuppressWarnings("unused") fail
    }
    private GameStatus status;
    private final Balls balls;
    private final Table table;
    private final Canvas canvas;
    private final GameStrategyContext fGameStrategyContext;
    private final Drawable rootDrawables = new DummyDrawable();
    private List<Movable> movables;
    @SuppressWarnings("FieldMayBeFinal")
    private List<Ball> BallsToRemove;

    public Game(Canvas canvas){
        BaseConfigFactory tableFactory = new TableFactory();
        BaseConfigFactory ballsFactory = new BallFactory();
        table = (Table)tableFactory.CreateGameObject();
        balls = (Balls) ballsFactory.CreateGameObject();
        this.canvas = canvas;
        movables = balls.getBalls();
        BallsToRemove = new ArrayList<>();
        status = GameStatus.start;
        this.canvas.setFocusTraversable(true);
        this.canvas.requestFocus();
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
        // TODO: Add drawable game objects to group calling Drawable::addToGroup(groupChildren)
        table.addToGroup(groupChildren);
        balls.addToGroup(groupChildren);
        rootDrawables.addToGroup(groupChildren);

    }
    public void scheduleToRemove(Ball ball){
        this.BallsToRemove.add(ball);
    }
    public void removeBalls(){
        this.movables.removeAll(this.BallsToRemove);
        this.BallsToRemove.clear();
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
        TempBalls.removeAll(this.BallsToRemove);
        if(TempBalls.size() == 1 &&
                ((Ball) TempBalls.get(0)).getColor() == GameColor.white ){
            strategy = new LastBallFallIntoPocket();
            fGameStrategyContext.setStrategy(strategy);
            fGameStrategyContext.executeStrategy(this,ball);
        }
    }
    // tick() is called every frame, handle main game logic here
    public void tick() {
        // TODO: Implement game logic
        if(status == GameStatus.start) {
            for (Movable movable : movables) {
                movable.move();
                movable.slowDownForFriction(table.getFriction());
                movable.handleCollision(movables, table.getBounds());
                Boolean isFallIntoPocket = movable.DoPocketBehave(table.getPockets());
                if (isFallIntoPocket)
                    processBallFallIntoPocket((Ball) movable);
            }
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
