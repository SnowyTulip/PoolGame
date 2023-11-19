package PoolGame.configBuildFactory;

import PoolGame.IObject.GameObject;
import PoolGame.utils.ConfigReader;

public class BallFactory extends BaseConfigFactory {
    @Override
    public GameObject CreateGameObject() {
        return ConfigReader.getBallsFromConfig();
    }
}
