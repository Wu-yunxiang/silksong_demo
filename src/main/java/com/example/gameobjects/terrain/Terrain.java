package com.example.gameobjects.terrain;

import com.example.gameobjects.GameObject;
import com.example.scene.GameScene;
import com.example.math.Rect;

import java.nio.ReadOnlyBufferException;
import java.util.ArrayList;
import java.util.List;

/**
*地形，有普通部分也有危险部分（通过颜色区分）
 */
public class Terrain extends GameObject{

    private float totalHeight;
    private int trapCount;
    // 每根刺的宽度——表示单个刺在横向上的占用宽度
    private float spikeWidth;
    private float spikeHeight;

    // 每个陷阱刺的数量
    private int spikeCount;
    private Rect boundingBox;
    private List<Rect> trapBoundingBoxes;


    public Terrain(GameScene scene, int trapCount) {
        totalHeight = scene.GROUND_Y;
        this.trapCount = trapCount;
        boundingBox = new Rect(0, 0, scene.SCREEN_WIDTH, totalHeight);
        spikeHeight = totalHeight / 2;
        spikeWidth = 10;
        calculateTrapBoundingBoxes(scene.SCREEN_WIDTH, scene.GROUND_Y, trapCount);
    }

    public void calculateTrapBoundingBoxes(float sceneWidth, float groundY, int trapCount) {
        trapBoundingBoxes = new ArrayList<>();
        if (trapCount <= 0) {
            return;
        }
        float totalTrapWidth = sceneWidth / 3.0f;
        float trapWidth = totalTrapWidth / trapCount;
        
        // 计算每个陷阱包含的刺的数量
        spikeCount = (int)(trapWidth / spikeWidth);
        if (spikeCount < 1) {
            spikeCount = 1;
        }

        // 计算间隔
        float remainingSpace = sceneWidth - totalTrapWidth;
        
        // 间隔数量 = 陷阱数量 + 1 (两端有间隔)
        float gap = remainingSpace / (trapCount + 1);

        float currentX = gap;
        for (int i = 0; i < trapCount; i++) {
            trapBoundingBoxes.add(new Rect(currentX, totalHeight - spikeHeight , trapWidth, spikeHeight));
            currentX += trapWidth + gap;
        }
    }
    
    public List<Rect> getTrapBoundingBoxes() {
        return trapBoundingBoxes;
    }   

    public Rect getBoundingBox() {
        return boundingBox;
    }

    @Override
    public void update(float deltaTime, GameScene scene) {
    }

}
