package PoolGame.IObject;

import javafx.geometry.Bounds;
import javafx.scene.shape.Circle;

import java.util.List;

public interface Movable {
    double getXPos();
    double getYPos();
    double getXVel(); // Vel stands for velocity
    double getYVel();
    void setXPos(double xPos);
    void setYPos(double yPos);
    void setXVel(double xVel);
    void setYVel(double yVel);
    void move();
    void slowDownForFriction(double friction);

    void handleCollision(List<Movable> movables, Bounds tableBounds);

    Boolean DoPocketBehave(List <Circle> pockets);
}
