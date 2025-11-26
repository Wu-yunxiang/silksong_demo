package com.example.gameobjects.character.bodyparts;

import com.example.gameobjects.character.Character.CharacterMode;

/**
 * 身体部件基类 (Body Part Base Class)
 * 说明：
 * - 所有具体的身体部件（如头发、脸、手等）都继承自此类。
 * - 子类需自行定义状态枚举与像素配置映射。
 */
public abstract class BodyPart {

    public BodyPart() {
        // 构造函数
    }

    /**
     * 根据角色模式获取当前像素数据
     * @param mode 角色当前的模式 (CharacterMode)
     * @return 对应的像素映射 (PixelMap)
     */
    public abstract PixelMap getPixels(CharacterMode mode);
}
