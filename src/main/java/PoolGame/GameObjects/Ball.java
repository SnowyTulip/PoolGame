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
import javafx.scene.shape.StrokeType;

import java.util.List;

import static java.lang.Math.sqrt;

/**
 * Ball 对象,用来表示单个球对象
 * + 位置信息
 * + 速度
 * + 颜色、质量、进球次数
 * + 球的行为
 * --Builder: 内部类实现建造者模式
 * --建造时使用连续调用方式
 */
public class Ball implements Drawable, Movable {
    private final GameColor fcolor;
    //初始速度、位置信息
    private final double initialPosX;
    private final double initialPosY;
    private final double initalVelX;
    private final double initalVelY;
    //进球次数记录(用于蓝色球的复位)
    private int CountOfFallIntoPocket;
    //基本属性
    private double fVelX;
    private double fVelY;
    private final double fMass;
    private final Circle shape;
    private static final double Radius = 15.0;
    //打击的缩放系数
    private final double hitScale = 0.1;
    //拖动白球后形成的拖拽线
    //实线
    private final Line  DragLine;
    //虚线:表示球将会抵达的位置
    private final Line  DashLine;
    private Circle Dashshape;
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
        //使用此函数注册鼠标拖动球时的行为
        if(AllowHit()) {registerMouseAction();}
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
        genDashBall(group);
    }
    private void genDashBall(ObservableList<Node> group){
        this.Dashshape = new Circle(0,0,Radius);
        this.Dashshape.setStrokeType(StrokeType.CENTERED);
        this.Dashshape.setStrokeWidth(2);
        this.Dashshape.getStrokeDashArray().addAll(5d, 10d); // 设置虚线样式
        this.Dashshape.setVisible(false);
        this.Dashshape.setFill(Color.WHITE);
        group.add(this.Dashshape);
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

    /**
     * 此函数会根据摩擦力减少球的速度
     * 根据物理学的知识
     * @param friction 来自table对象的摩擦力
     */
    @Override
    public void slowDownForFriction(double friction) {
        this.friction = friction;
        double dt = 1;
        double Ff = this.friction * fMass * 0.1;//ignore g
        double V = sqrt(fVelX*fVelX + fVelY*fVelY);
        //当速度为0的时候应当不做任何处理返回
        //注意,速度可以小于0,因为矢量表示
        if(V == 0)return;
        double Fx = Ff * fVelX / V;
        double Fy = Ff * fVelY / V;
        // V' = V - a*t
        double newVelX = fVelX - (Fx / fMass) * dt;
        double newVelY = fVelY - (Fy / fMass) * dt;
        // if next tick is zero,just stop it,中英混用
        fVelX = newVelX * fVelX <= 0 ? 0 : newVelX;
        fVelY = newVelY * fVelY <= 0 ? 0 : newVelY;
    }

    /**
     * 处理碰撞的函数
     * 前半部分是处理球与球桌边缘的碰撞
     * 然后调用助教提供的碰撞函数(defined in util.java)处理碰撞
     * note: 应该为球添加一个标记.以减少重复的计算
     * @param movables 球
     * @param tableBounds 桌子边缘的Bounds对象
     */
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

    /**
     * 实际上此函数有缺失,仅仅只是让球不可见
     * 让球不在发生碰撞的逻辑定义在Game.java中，将球直接删除
     * @param enable 设置球是否可用
     */
    public void setEnable(Boolean enable){
        this.shape.setVisible(enable);
    }
    public int getCountOfFallIntoPocket(){
        return this.CountOfFallIntoPocket;
    }

    /**
     * 此处是判断球是否会进入球洞
     * 具体球的落入球洞的行为在策略类中定义
     * note:实际上不应该在此处对球进洞的次数做更改，而是仅判断是否进球
     * @param pockets 球洞集合
     * @return 是否进球洞
     */
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

    /**
     * 缩放        Vx' = Vx / Max(Vel**0.5 , 1)
     * @param Vel 总体速度 Vel = sqrt(Vx*Vx + Vy*Vy)
     * @param Vx  X方向速度
     * @return    缩放后的Vx‘
     */
    public double scaleVel(double Vel,double Vx){
        return Vx/ Math.max(Math.pow(Vel,0.5),1);
    }

    /**
     * 绑定鼠标事件
     * 策略:
     *     +当用户拖拽鼠标时 -> 绘制球中心到鼠标的实线辅助线
     *     +当用户拖拽鼠标时 -> 计算球可能会被打击的位置,绘制虚线辅助线
     * 计算公式见高中物理必修1
     */

    public void registerMouseAction() {

        this.shape.setOnMousePressed(e -> {
            //球杆击中球采用这个
//            dragRelativeX = e.getSceneX();
//            dragRelativeY = e.getSceneY();
//            System.out.println("Relative is" + "(" + dragRelativeX + "," + dragRelativeY + ")");
        });
        this.shape.setOnMouseDragged(e -> {
            //isStop : 只有白球停止才允许下一次击球
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

                this.Dashshape.setVisible(true);
                this.Dashshape.setCenterX(this.shape.getCenterX() + Dx);
                this.Dashshape.setCenterY(this.shape.getCenterY() + Dy);
            }
        });
        this.shape.setOnMouseReleased(e -> {
            if(isStop()) {
                this.Dashshape.setVisible(false);
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
