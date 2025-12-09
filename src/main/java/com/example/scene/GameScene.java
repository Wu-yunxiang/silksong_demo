package com.example.scene;

import java.util.List;

import com.example.gameobjects.GameObject;
import com.example.gameobjects.character.Character;
import com.example.renderer.Renderer;

import java.util.ArrayList;

/**
 * 游戏主场景。
 */
public class GameScene {
    //背景中的游戏对象
    private List<GameObject> gameObjects;
    public GameScene(){ 
        gameObjects = new ArrayList<>();
    }

    public Character getCharacter() {
        for (GameObject obj : gameObjects) {
            if (obj instanceof Character) {
                return (Character) obj;
            }
        }
        return null;
    }

    public void render(){
        Renderer.render();
    }

    public void removeGameObject(GameObject obj) {
        gameObjects.remove(obj);
    }

    public void addGameObject(GameObject obj) {
        gameObjects.add(obj);
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public long getWindowHandle() {
        return Renderer.getWindowHandle();
    }
}
