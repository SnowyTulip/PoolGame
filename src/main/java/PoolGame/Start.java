package PoolGame;

import PoolGame.IObject.GameObject;
import PoolGame.utils.ConfigReader;

public class Start {
    public static void main(String[] args) {
        GameObject table = ConfigReader.getTableFromConfig();
        GameObject balls = ConfigReader.getBallsFromConfig();
        App.main(args);
    }
}
