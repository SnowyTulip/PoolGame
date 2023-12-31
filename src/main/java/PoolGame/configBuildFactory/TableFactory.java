package PoolGame.configBuildFactory;

import PoolGame.IObject.GameObject;
import PoolGame.utils.ConfigReader;

public class TableFactory extends BaseConfigFactory {
    /**
     *
     * @return Table
     */
    @Override
    public GameObject CreateGameObject() {
        return ConfigReader.getTableFromConfig();
    }
}
