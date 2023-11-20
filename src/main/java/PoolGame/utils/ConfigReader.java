package PoolGame.utils;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import PoolGame.GameObjects.Ball;
import PoolGame.GameObjects.Balls;
import PoolGame.GameObjects.Table;
import PoolGame.IObject.GameColor;
import PoolGame.IObject.GameObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * 使用助教提供的ConfigReader.java中的 parse()函数进行修改
 * 从配置文件中读取table和球的配置
 */
public class ConfigReader {

    public static final String  configPath = "src/main/resources/config.json";
    public static final String TABLE_FILED = "Table";
    public static final String COLOR_FIELD = "colour";
    public static final String FRICTION_FILED = "friction";
    public static final String SIZE_FILED = "size";
    public static final String X_FILED = "x";
    public static final String Y_FILED = "y";
    public static final String BALLS_FILED = "Balls";
    public static final String BALL_FILED = "ball";
    public static final String POS_FILED = "position";
    public static final String VEL_FILED = "velocity";
    public static final String MASS_FILED = "mass";
    public static GameObject getTableFromConfig(){
        JSONParser parser = new JSONParser();
        GameObject table = null;
        try {
            Object object = parser.parse(new FileReader(configPath));
            JSONObject jsonObject = (JSONObject) object;
            JSONObject jsonTable = (JSONObject) jsonObject.get(TABLE_FILED);
            GameColor tableGameColor =  getColor((String)jsonTable.get(COLOR_FIELD));
            Long tableX = (Long) ((JSONObject) jsonTable.get(SIZE_FILED)).get(X_FILED);
            Long tableY = (Long) ((JSONObject) jsonTable.get(SIZE_FILED)).get(Y_FILED);
            Double tableFriction = (Double) jsonTable.get(FRICTION_FILED);

            table = new Table(tableGameColor,tableFriction,tableX,tableY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return table;
    }
    public static GameObject getBallsFromConfig(){
        JSONParser parser = new JSONParser();
        Balls balls = new Balls();
        try {
            Object object = parser.parse(new FileReader(configPath));
            JSONObject jsonObject = (JSONObject) object;
            JSONObject jsonBalls = (JSONObject) jsonObject.get(BALLS_FILED);

            JSONArray jsonBallsBall = (JSONArray) jsonBalls.get(BALL_FILED);

            for (Object obj : jsonBallsBall) {
                JSONObject jsonBall = (JSONObject) obj;
                GameColor gameColor =  getColor((String)jsonBall.get(COLOR_FIELD));
                Double positionX = (Double) ((JSONObject) jsonBall.get(POS_FILED)).get(X_FILED);
                Double positionY = (Double) ((JSONObject) jsonBall.get(POS_FILED)).get(Y_FILED);
                Double velocityX = (Double) ((JSONObject) jsonBall.get(VEL_FILED)).get(X_FILED);
                Double velocityY = (Double) ((JSONObject) jsonBall.get(VEL_FILED)).get(Y_FILED);
                Double mass = (Double) jsonBall.get(MASS_FILED);
                Ball ball = new Ball.Builder()
                        .setColor(gameColor)
                        .setPosX(positionX).setPosY(positionY)
                        .setVelX(velocityX).setVelY(velocityY)
                        .setMass(mass).BuildBallFinish();
                balls.addBall(ball);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return balls;
    }
    public static GameColor getColor(String ColorStr){
        String ops = ColorStr.toLowerCase();
        switch (ops) {
            case "white" -> {
                return GameColor.white;
            }
            case "red" -> {
                return GameColor.red;
            }
            case "blue" -> {
                return GameColor.blue;
            }
            case "green" -> {
                return GameColor.green;
            }
            default -> {
            }
        }
        return null;
    }


    public static void main(String[] args) {
        // if a command line argument is provided, that should be used as the path
        // if not, you can hard-code a default. e.g. "src/main/resources/config.json"
        // this makes it easier to test your program with different config files
//        String configPath;
//        if (args.length > 0) {
//            configPath = args[0];
//        } else {
//            configPath = "src/main/resources/config.json";
//        }
    }

}
