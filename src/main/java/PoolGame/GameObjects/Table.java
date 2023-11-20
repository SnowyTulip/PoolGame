package PoolGame.GameObjects;

import PoolGame.IObject.Drawable;
import PoolGame.IObject.GameColor;
import PoolGame.IObject.GameObject;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Table类 表示桌面
 */
public class Table implements GameObject , Drawable {
    private final GameColor fcolor;
    private final double friction;
    private final Rectangle shape;
    private List<Circle> pockets;
    private final Bounds tableBounds;
    @SuppressWarnings("FieldCanBeLocal")
    private final double PocketRadius = 30;

    public Table(GameColor gameColor, double Friction, long sizeX, long sizeY){
        fcolor = gameColor;
        friction = Friction;
        shape = new Rectangle(sizeX,sizeY,Color.valueOf(fcolor.name()));
        tableBounds = shape.getBoundsInLocal();
        InitPockets(sizeX,sizeY);
    }

    /**
     * 根据球桌大小放置6个球洞
     * @param sizeX 球桌的长
     * @param sizeY 球桌的宽
     */
    private void InitPockets(long sizeX,long sizeY){
        pockets = new ArrayList<>();
        pockets.add(new Circle(0,0,PocketRadius));
        pockets.add(new Circle(0,sizeY,PocketRadius));
        pockets.add(new Circle(sizeX,0,PocketRadius));
        pockets.add(new Circle(sizeX,sizeY,PocketRadius));
        pockets.add(new Circle(sizeX/2.0,0,PocketRadius));
        pockets.add(new Circle(sizeX/2.0,sizeY,PocketRadius));
    }
    public List<Circle> getPockets(){
        return this.pockets;
    }
    @SuppressWarnings("unused")
    public GameColor getColor() {
        return fcolor;
    }
    public double getFriction(){
        return friction;
    }
    public Bounds getBounds(){
        return tableBounds;
    }

    @Override
    public Node getNode() {
        return this.shape;
    }
    @Override
    public void addToGroup(ObservableList<Node> group) {
        group.add(this.shape);
        group.addAll(this.pockets);
    }
}
