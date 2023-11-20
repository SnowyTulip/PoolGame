package PoolGame.GameObjects;

import PoolGame.IObject.GameObject;
import PoolGame.IObject.Movable;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Balls implements GameObject {
    @SuppressWarnings("FieldMayBeFinal")
    private List<Ball> balls = new ArrayList<>();

    public void addBall(Ball ball) {
        balls.add(ball);
    }

    public List<Movable> getBalls() {
        return balls.stream().map(ball -> (Movable) ball).collect(Collectors.toList());
    }

    public void addToGroup(ObservableList<Node> groupChildren) {
        for (Ball ball:balls) {
            ball.addToGroup(groupChildren);
        }
    }
}