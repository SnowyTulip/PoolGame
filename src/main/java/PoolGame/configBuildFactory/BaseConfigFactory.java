package PoolGame.configBuildFactory;

import PoolGame.IObject.GameObject;

public abstract class BaseConfigFactory {
    /**
     *
     * @return GameObject对象 : Table 和 Balls
     */
    public abstract GameObject CreateGameObject();
}
