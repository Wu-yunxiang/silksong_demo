package com.example.scene;

import java.util.List;
import java.util.ArrayList;
import com.example.gameobjects.GameObject;

/**
 * 场景基类（抽象）。
 *
 * 说明：
 * - 所有具体场景（菜单、游戏内部、暂停、结算等）都应继承自此类。
 * - 按你的要求，目前仅声明类和中文注释，不包含字段或方法，实现逻辑由你后续逐步添加。
 */
public class Scene {
    private List<GameObject> gameObjects;
    public Scene() {
        gameObjects = new ArrayList<>();
    }
    
    public List<GameObject> getGameObjects() {
        return gameObjects;
    }
}