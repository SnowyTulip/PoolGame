package PoolGame.GameObjects;

import PoolGame.IObject.Drawable;
import PoolGame.IObject.GameColor;
import PoolGame.IObject.Movable;
import PoolGame.utils.util;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.List;

import static java.lang.Math.sqrt;

public class Ball implements Drawable, Movable {
    private final GameColor fcolor;
    private final double initialPosX;
    private final double initialPosY;
    private final double initalVelX;
    private final double initalVelY;
    private int CountOfFallIntoPocket;
    private double fVelX;
    private double fVelY;
    private final double fMass;
    private final Circle shape;
    private static final double Radius = 15.0;
    //打击的缩放系数
    private final double hitScale = 0.1;
    private final Line  DragLine;
    private final Line  DashLine;
    private double friction = 1;
    public static class Builder{
        private GameColor fcolor;
        private double fPosX;
        private double fPosY;

        private double fVelX;
        private double fVelY;
        private double fMass;
        //都是必选参数
        public Builder setColor(GameColor gameColor){
            this.fcolor = gameColor;
            return this;
        }
        public Builder setPosX(double PosX){
            this.fPosX = PosX;
            return  this;
        }
        public Builder setPosY(double PosY){
            this.fPosY = PosY;
            return  this;
        }
        public Builder setVelX(double VelX){
            this.fVelX = VelX;
            return this;
        }
        public Builder setVelY(double VelY){
            this.fVelY = VelY;
            return this;
        }
        public Builder setMass(double Mass){
            this .fMass = Mass;
            return this;
        }
        public Ball BuildBallFinish(){
            return new Ball(this);
        }
    }
    private Ball(Builder builder){
        this.fcolor = builder.fcolor;
        this.fVelX  = builder.fVelX;
        this.fVelY  = builder.fVelY;
        this.initalVelX = builder.fVelX;
        this.initalVelY = builder.fVelY;
        this.fMass  = builder.fMass;
        this.initialPosX = builder.fPosX;
        this.initialPosY = builder.fPosY;
        this.CountOfFallIntoPocket = 0;
        this.shape = new Circle(builder.fPosX,builder.fPosY,Radius);
        this.shape.setFill(Color.valueOf(fcolor.name()));
        this.DragLine = new Line();
        this.DragLine.setVisible(false);
        this.DragLine.setFill(Color.color(1,1,1));

        this.DashLine = new Line();
        this.DashLine.setVisible(false);
        this.DashLine.getStrokeDashArray().addAll(5d,5d);
        this.DashLine.setFill(Color.color(1,1,1));

        if(AllowHit()) registerMouseAction();
    }
    @Override
    public Node getNode() {
        return this.shape;
    }

    @Override
    public void addToGroup(ObservableList<Node> group) {
        group.add(this.shape);
        group.add(this.DragLine);
        group.add(this.DashLine);
    }
    public void goBackToInitialPosition(){
        this.shape.setCenterX(this.initialPosX);
        this.shape.setCenterY(this.initialPosY);
        this.fVelX = this.initalVelX;
        this.fVelY = this.initalVelY;
    }
    public GameColor getColor(){
        return this.fcolor;
    }
    public void reset(){
        this.shape.setCenterX(this.initialPosX);
        this.shape.setCenterY(this.initialPosY);
        this.CountOfFallIntoPocket = 0;
        this.fVelX = this.initalVelX;
        this.fVelY = this.initalVelY;
        this.shape.setVisible(true);
    }
    @Override
    public double getXPos() {
        return this.shape.getCenterX();
    }

    @Override
    public double getYPos() {
        return this.shape.getCenterY();
    }

    @Override
    public double getXVel() {
        return fVelX;
    }

    @Override
    public double getYVel() {
        return fVelY;
    }
    public double getMass(){
        return this.fMass;
    }

    @Override
    public void setXPos(double xPos) {
        this.shape.setCenterX(xPos);
    }

    @Override
    public void setYPos(double yPos) {
        this.shape.setCenterY(yPos);
    }

    @Override
    public void setXVel(double xVel) {
        fVelX = xVel;
    }

    @Override
    public void setYVel(double yVel) {
        fVelY = yVel;
    }

    @Override
    public void move() {
        double xPos = getXPos() + getXVel();
        double yPos = getYPos() + getYVel();
        setXPos(xPos);
        setYPos(yPos);
    }

    @Override
    public void slowDownForFriction(double friction) {
        this.friction = friction;
        double dt = 1;
        double Ff = this.friction * fMass * 0.1;//ignore g
        double V = sqrt(fVelX*fVelX + fVelY*fVelY);
        //if v is zero just return
        if(V == 0)return;
        double Fx = Ff * fVelX / V;
        double Fy = Ff * fVelY / V;
        // V' = V - a*t
        double newVelX = fVelX - (Fx / fMass) * dt;
        double newVelY = fVelY - (Fy / fMass) * dt;
        // if next tick is zero,just stop it
        fVelX = newVelX * fVelX <= 0 ? 0 : newVelX;
        fVelY = newVelY * fVelY <= 0 ? 0 : newVelY;
    }

    @Override
    public void handleCollision(List<Movable> movables,Bounds tableBounds) {
        Bounds ballBounds = this.getNode().getBoundsInLocal();
        if (!tableBounds.contains(ballBounds)) {
            if (ballBounds.getMaxX() >= tableBounds.getMaxX()) {
                setXVel(-getXVel());
                setXPos(tableBounds.getMaxX() - Radius);
            } else if (ballBounds.getMinX() <= tableBounds.getMinX()){
                setXVel(-getXVel());
                setXPos(tableBounds.getMinX() + Radius);
            }
            if (ballBounds.getMaxY() >= tableBounds.getMaxY()) {
                setYVel(-getYVel());
                setYPos(tableBounds.getMaxY() - Radius);
            } else if (ballBounds.getMinY() <= tableBounds.getMinY()) {
                setYVel(-getYVel());
                setYPos(tableBounds.getMinY() + Radius);
            }
        }
        for (Movable other :movables) {
            if(other == this) continue;
            Bounds otherBounds = ((Ball) other).getNode().getBoundsInLocal();
            if (ballBounds.intersects(otherBounds)) {
                util.processBall2BallCollision(this, (Ball) other);
            }
        }
    }
    public void setEnable(Boolean enable){
        this.shape.setVisible(enable);
    }
    public int getCountOfFallIntoPocket(){
        return this.CountOfFallIntoPocket;
    }
    @Override
    public Boolean DoPocketBehave(List<Circle> pockets) {
        boolean fallIntoPockets = false;
        double posX = getXPos();
        double posY = getYPos();
        for (Circle pocket:pockets) {
            double pocketX  = pocket.getCenterX();
            double pocketY  = pocket.getCenterY();
            double distance = Math.sqrt((posX - pocketX)*(posX - pocketX)  + (posY - pocketY)*(posY - pocketY));
            if(distance < pocket.getRadius()){
                fallIntoPockets = true;
                this.CountOfFallIntoPocket += 1;
//                this.setEnable(false);
                break;
            }
        }
        return fallIntoPockets;
    }
    public boolean isStop(){
        return this.getXVel() == 0 && this.getYVel() == 0;
    }

    public Boolean AllowHit(){
        return this.fcolor == GameColor.white;
    }
    public double scaleVel(double Vel,double Vx){
        return Vx/ Math.max(Math.pow(Vel,0.5),1);
    }
    public void registerMouseAction() {
        this.shape.setOnMousePressed(e -> {
            //球杆击中球采用这个
//            dragRelativeX = e.getSceneX();
//            dragRelativeY = e.getSceneY();
            //TODO 判断鼠标点是否在合理的范围内
//            System.out.println("Relative is" + "(" + dragRelativeX + "," + dragRelativeY + ")");
        });
        this.shape.setOnMouseDragged(e -> {
            if(isStop()) {
                this.DragLine.setStartX(this.shape.getCenterX());
                this.DragLine.setStartY(this.shape.getCenterY());
                this.DragLine.setEndX(e.getSceneX());
                this.DragLine.setEndY(e.getSceneY());
                this.DragLine.setVisible(true);

                // Just have a trick,Calculate the distance
                double Vx = (this.shape.getCenterX() - e.getSceneX()) * hitScale;
                double Vy = (this.shape.getCenterY() - e.getSceneY()) * hitScale;
                double V  = Math.sqrt(Vx*Vx + Vy*Vy);
                //需要做一些缩放
                Vx = scaleVel(V,Vx);
                Vy = scaleVel(V,Vy);
                V  = Math.sqrt(Vx*Vx + Vy*Vy);

                double Dx = Vx * V / (2 * friction * 0.1) ;
                double Dy = Vy * V / (2 * friction * 0.1) ;
                this.DashLine.setStartX(this.shape.getCenterX());
                this.DashLine.setStartY(this.shape.getCenterY());
                this.DashLine.setEndX(this.shape.getCenterX() + Dx);
                this.DashLine.setEndY(this.shape.getCenterY() + Dy);
                this.DashLine.setVisible(true);
            }
        });
        this.shape.setOnMouseReleased(e -> {
            if(isStop()) {
                double Vx = (this.shape.getCenterX() - e.getSceneX()) * hitScale;
                double Vy = (this.shape.getCenterY() - e.getSceneY()) * hitScale;
                double V  = Math.sqrt(Vx*Vx + Vy*Vy);
                //需要做一些缩放
                Vx = scaleVel(V,Vx);
                Vy = scaleVel(V,Vy);
                setXVel(Vx);
                setYVel(Vy);
                // 发送球体出去
                this.DragLine.setVisible(false);
                this.DashLine.setVisible(false);
            }
        });
    }
}
