package PoolGame.configBuildFactory;

import PoolGame.IObject.GameObject;
import PoolGame.utils.ConfigReader;

public class BallFactory extends BaseConfigFactory {
    /**
     *
     * @return Balls对象 由Config文件读取
     */
    @Override
    public GameObject CreateGameObject() {
        return ConfigReader.getBallsFromConfig();
    }
}
