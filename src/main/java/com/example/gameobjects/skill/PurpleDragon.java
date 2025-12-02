package com.example.gameobjects.skill;

import com.example.math.Rect;
import com.example.math.Vector2;
import com.example.scene.GameScene;
import com.example.gameobjects.GameObject;

/**
 * 黑波技能 (Black Wave Skill)
 * 说明：名称为 “黑波”，作为游戏中某个具体技能实体。
 */
public class PurpleDragon extends GameObject {
    private Rect boundingBox;
    private float velocityX = PurpleDragonConfig.SPEED;            // 速度 (Velocity)
    private Vector2 basePosition;        // 基准位置 (贴图左下角坐标)

    public PurpleDragon(Vector2 ownerPostition) {
        this.boundingBox = new Rect(ownerPostition.x, ownerPostition.y,
                PurpleDragonConfig.WIDTH, PurpleDragonConfig.HEIGHT);
        this.basePosition = new Vector2(ownerPostition.x, ownerPostition.y);
    }

    @Override
    public void update(float deltaTime, GameScene scene) {
        // 黑波技能每帧更新
        basePosition.x += velocityX * deltaTime;
        boundingBox.setPosition(basePosition.x, basePosition.y);
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }
}
