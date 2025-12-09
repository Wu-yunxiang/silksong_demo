package com.example.gameobjects.skill;

import com.example.math.Rect;
import com.example.math.Vector2;
import com.example.scene.GameScene;
import com.example.gameobjects.GameObject;
import com.example.gameobjects.character.Character;
import com.example.pictureconfig.CharacterPicturesInformation;
import com.example.pictureconfig.GameSceneConfig;
import com.example.gameobjects.character.CharacterConfig;
import org.lwjgl.opengl.GL11;

/**
 * 龙波对象 (Purple Dragon Object)
 */
public class PurpleDragon extends GameObject {
    private Vector2 position;        
    private boolean orientation;      // true为向右，false为向左
    private boolean isAlive;
    private boolean isComplete;         //形态
    private Character owner;          // 释放者
    private float speed;

    public PurpleDragon(Character owner) {
        this.orientation = (owner.getOrientation() == Character.Orientation.RIGHT);
        this.isAlive = true;
        this.isComplete = false;
        this.owner = owner;
        this.speed = 0;

        // 计算横坐标：owner CAST_SKILL 状态的 skillStartActionNum 的图片右边界在界面里的横坐标
        CharacterPicturesInformation.PictureInformation pi =CharacterPicturesInformation.characterPicturesInfo.get(Character.CharacterBehavior.CAST_SKILL).get(CharacterConfig.skillStartActionNum - 1);
        if (this.orientation) { // RIGHT
            this.position.x = owner.getPosition().x + pi.pictureSize.x - pi.basePosition.x;
        } else { // LEFT
            this.position.x = owner.getPosition().x + pi.basePosition.x - pi.pictureSize.x;
        }

        // 纵坐标：使得 skillPicturesInfo[0] 的模型底部落在地面边界上
        this.position.y = GameSceneConfig.GroundHeight + PurpleDragonConfig.skillPicturesInfo[0].basePosition.y;

        owner.setPurpleDragon(this);
    }

    @Override
    public void update(float deltaTime, GameScene scene) { // 每帧更新
        if(!isComplete){
            if(owner.getActionNum() >= CharacterConfig.skillCompleteActionNum){
                isComplete = true;
                speed = PurpleDragonConfig.SPEED;
            }else {
                return;
            }
        }

        float distance = speed * deltaTime;
        if (orientation) { // RIGHT
            position.x += distance;
        } else { // LEFT
            position.x -= distance;
        }
    }

    @Override
    public void render() {
        PurpleDragonConfig.PictureInformation info = PurpleDragonConfig.skillPicturesInfo[this.isComplete ? 1 : 0];
        int texId = info.textureId;
        float w = info.pictureSize.x;
        float h = info.pictureSize.y;
        float baseX = info.basePosition.x;
        float baseY = info.basePosition.y;

        float drawX;
        if (this.orientation) {
            drawX = this.position.x - baseX;
        } else {
            drawX = this.position.x - (w - baseX);
        }
        float drawY = this.position.y - baseY;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        GL11.glBegin(GL11.GL_QUADS);
        if (this.orientation) {
            GL11.glTexCoord2f(0f, 0f); GL11.glVertex2f(drawX, drawY);
            GL11.glTexCoord2f(1f, 0f); GL11.glVertex2f(drawX + w, drawY);
            GL11.glTexCoord2f(1f, 1f); GL11.glVertex2f(drawX + w, drawY + h);
            GL11.glTexCoord2f(0f, 1f); GL11.glVertex2f(drawX, drawY + h);
        } else {
            GL11.glTexCoord2f(1f, 0f); GL11.glVertex2f(drawX, drawY);
            GL11.glTexCoord2f(0f, 0f); GL11.glVertex2f(drawX + w, drawY);
            GL11.glTexCoord2f(0f, 1f); GL11.glVertex2f(drawX + w, drawY + h);
            GL11.glTexCoord2f(1f, 1f); GL11.glVertex2f(drawX, drawY + h);
        }
        GL11.glEnd();
    }

    public Rect getBoundingBox() {
        PurpleDragonConfig.PictureInformation info = this.isComplete ? PurpleDragonConfig.skillPicturesInfo[1] : PurpleDragonConfig.skillPicturesInfo[0];
        if(orientation){
            return new Rect(
                position.x - info.basePosition.x + info.boundingBox.x,
                position.y - info.basePosition.y + info.boundingBox.y,
                info.boundingBox.width,
                info.boundingBox.height
            );
        }else {
            return new Rect(
                position.x + info.basePosition.x - info.boundingBox.x - info.boundingBox.width,
                position.y - info.basePosition.y + info.boundingBox.y,
                info.boundingBox.width,
                info.boundingBox.height
            );
        }
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
    
    public boolean isAlive() {
        return isAlive;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }
}
