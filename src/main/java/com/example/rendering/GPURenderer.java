package com.example.rendering;

import com.example.gameobjects.character.Character;
import com.example.gameobjects.character.bodyparts.BodyPart;
import com.example.gameobjects.character.bodyparts.PixelMap;
import com.example.gameobjects.character.bodyparts.PixelMap.PixelData;
import com.example.scene.Scene;
import java.util.List;

/**
 * GPU 渲染器（GPURenderer）。
 *
 * 说明：
 * - 基于 GPU 的渲染实现，理想情况下使用 OpenGL / Vulkan / DirectX 等图形 API 做硬件加速渲染。
 * - 当前仅声明类并实现 `Renderer` 接口（空实现），后续你可以让我在此处添加渲染管线与字段。
 */
public class GPURenderer implements Renderer {

    public void render(Scene scene) {
        // TODO: Iterate over scene objects and render them
        // For now, we assume we have access to the character from the scene
        // Character character = scene.getCharacter(); 
        // renderCharacter(character);
    }

    public void renderCharacter(Character character) {
        List<BodyPart> parts = character.getBodyParts();
        Character.CharacterMode mode = character.getMode();

        for (BodyPart part : parts) {
            // 直接根据 CharacterMode 获取像素数据，具体的 State 映射由各 BodyPart 子类内部处理
            PixelMap pixelMap = part.getPixels(mode);
            
            if (pixelMap != null && pixelMap.pixels != null) {
                for (PixelData pixel : pixelMap.pixels) {
                    drawPixel(pixel.relativeX, pixel.relativeY, pixel.r, pixel.g, pixel.b, pixel.a);
                }
            }
        }
    }

    private void drawPixel(int x, int y, int r, int g, int b, int a) {
        // Placeholder for actual GPU draw call
        // System.out.println("Drawing pixel at " + x + ", " + y + " color: " + r + "," + g + "," + b);
    }
}
