package com.example.gameobjects.skill;

import com.example.math.Rect;
import com.example.math.Vector2;

/**
 * 存储 PurpleDragon 技能相关的配置常量
 */
public class PurpleDragonConfig {
    // 速度 (像素/秒)
    public static final float SPEED = 800.0f;
    // 消耗能量
    public static final int ENERGY_COST = 3;
    // 伤害值
    public static final int DAMAGE = 2;

    public static PictureInformation[] skillPicturesInfo;

    private PurpleDragonConfig() {
    }

    public static class PictureInformation{
        public int textureId; 
        public Vector2 pictureSize; 
        public Vector2 basePosition; 
        public Rect boundingBox; 
        public int frames; 
        public PictureInformation(int textureId, Vector2 pictureSize, Vector2 basePosition, Rect boundingBox) {
            this.textureId = textureId;
            this.pictureSize = pictureSize;
            this.basePosition = basePosition;
            this.boundingBox = boundingBox;
            this.frames = -1;
        }
    }
}
