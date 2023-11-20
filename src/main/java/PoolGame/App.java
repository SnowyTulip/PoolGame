package PoolGame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App  extends Application{

    private static final String TITLE = "PoolGame";
    private static final double DIM_X = 1135;
    private static final double DIM_Y = 635;
    private static final double FRAMETIME = 1.0/120.0;


//    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle(TITLE);
        stage.show();
        stage.setWidth(DIM_X);
        stage.setHeight(DIM_Y);

        // setup drawables
        Canvas canvas = new Canvas(DIM_X, DIM_Y);
        root.getChildren().add(canvas);
        Game game = new Game(canvas);
        game.addDrawables(root);

        // setup frames
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame frame = new KeyFrame(Duration.seconds(FRAMETIME), (actionEvent) -> game.tick());
        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
