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
    private List<GameObject> gameObjects;
    public GameScene() {
        gameObjects = new ArrayList<>();
    }
    public void render() {
        //render 背景
        for (GameObject obj : gameObjects) {
            if(obj.isRendered){
                continue;
            }
            obj.render();
        }
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
