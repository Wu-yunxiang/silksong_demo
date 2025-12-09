package com.example.pictureconfig;

import com.example.gameobjects.skill.PurpleDragonConfig;
import com.example.math.Vector2;
import com.example.math.Rect;
import com.example.renderer.Renderer;

public class PurpleDragonConfigLoader {
    private PurpleDragonConfigLoader() {}

    /**
     * 加载 PurpleDragon 的两种状态贴图（1.png = start, 2.png = complete）
     * 填充到 PurpleDragonConfig.skillPicturesInfo 中
     */
    public static void loadPurpleDragonConfig() {
        String[] resources = new String[]{
            "/PurpleDragon_Standardized_Final/1.png",
            "/PurpleDragon_Standardized_Final/2.png"
        };

        PurpleDragonConfig.skillPicturesInfo = new PurpleDragonConfig.PictureInformation[resources.length];

        for (int i = 0; i < resources.length; i++) {
            String res = resources[i];
            int[] texInfo = Renderer.loadTextureInfoFromClasspath(res);
            int texId = texInfo[0];
            int w = texInfo[1];
            int h = texInfo[2];

            Vector2 pictureSize = new Vector2(w, h);
            Vector2 basePosition;
            Rect boundingBox;

            if(i==0){
                basePosition = new Vector2(28, h - 108); //基准位置在Box左侧中间，相对于左下角，硬编码
                boundingBox = new Rect(28, h - 195, 145 - 28, 195 - 20);
            }else {
                basePosition = new Vector2(547, h - 74); //基准位置在Box右侧中间，相对于左下角，硬编码
                boundingBox = new Rect(11, h - 128, 547 - 11, 128 - 19);
            }

            PurpleDragonConfig.skillPicturesInfo[i] = new PurpleDragonConfig.PictureInformation(texId, pictureSize, basePosition, boundingBox);
        }
    }
}
