package com.example.pictureconfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.gameobjects.character.Character.CharacterBehavior;
import com.example.math.Vector2;
import com.example.math.Rect;

public class CharacterPicturesInformation {
    public static Map<CharacterBehavior, List<PictureInformation>> characterPicturesInfo = new HashMap<>();

    private CharacterPicturesInformation() {
    }

    public static class PictureInformation {
        public int textureId; //纹理ID,用于渲染
        public Vector2 pictureSize; //贴图尺寸
        public Vector2 basePosition; //相对于左下角的基准位置
        public Rect hitBox;  //相对于左下角的受击框
        public Rect attackBox; //相对于左下角的攻击框
        public int frames; //持续帧数

        public PictureInformation(int textureId, Vector2 pictureSize, Vector2 basePosition, Rect hitBox, Rect attackBox) {
            this.textureId = textureId;
            this.pictureSize = pictureSize;
            this.basePosition = basePosition;
            this.hitBox = hitBox;
            this.attackBox = attackBox;
            this.frames = -1;
        }
    }
}
