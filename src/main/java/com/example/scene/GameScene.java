package com.example.scene;

import java.util.List;

import com.example.gameobjects.GameObject;
import com.example.gameobjects.character.Character;
import java.util.ArrayList;

/**
 * 游戏主场景。
 *
 * 说明：
 * - 表示实际游戏运行的场景，承载角色/敌人/地形/计时/判定等逻辑。
 * - 目前仅作类声明，不包含实现细节，后续由你指示逐步完善。
 */
public class GameScene {
    //背景对象
    public final float SCREEN_WIDTH = 800;
    public final float SCREEN_HEIGHT = 600;
    public final float GROUND_Y = 50;
    private List<GameObject> gameObjects;
    public GameScene() {
        gameObjects = new ArrayList<>();
    }

    public Character getMainCharacter() {
        for (GameObject obj : gameObjects) {
            if (obj instanceof Character) {
                return (Character) obj;
            }
        }
        return null;
    }

    public void addGameObject(GameObject obj) {
        gameObjects.add(obj);
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }
}
