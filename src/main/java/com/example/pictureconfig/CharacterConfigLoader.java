package com.example.pictureconfig;

import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import com.example.gameobjects.character.Character.CharacterBehavior;
import com.example.math.Vector2;
import com.example.math.Rect;
import java.util.ArrayList;

public class CharacterConfigLoader {

    public static void loadCharacterConfigs() {
        Path characterPath = Paths.get("src", "main", "resources", "Character_Standardized_Final");
        for(CharacterBehavior behavior : CharacterBehavior.values()){
            Path picturePath = characterPath.resolve(behavior.name());
            Path behaviorPath = characterPath.resolve(behavior.name()).resolve("Description");
            int behaviorNum = 0;
            while(true){
                Path behaviorNumPath = behaviorPath.resolve(++behaviorNum + ".txt");
                if(!Files.exists(behaviorNumPath)){
                    break;
                }
                Path pictureNumPath = picturePath.resolve(behaviorNum + ".png");
                //具体动作编号的图片，路径，动作，动作的具体编号
                loadPictureInformation(pictureNumPath, behaviorNumPath, behavior, behaviorNum);
            }
        }
    }

    private static void loadPictureInformation(Path pictureNumPath, Path behaviorNumPath, CharacterBehavior behavior, int behaviorNum) {
        try {
            List<String> lines = Files.readAllLines(behaviorNumPath);
            String line1 = lines.get(0);
            String line2 = lines.get(1);
            String line3 = lines.get(2);

            int[] result1 = parseInts(line1);
            int[] result2 = parseInts(line2);
            int[] result3 = parseInts(line3);

            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(pictureNumPath.toFile());
            Vector2 pictureSize = new Vector2(img.getWidth(), img.getHeight());
            result1[1] = (int) pictureSize.y - result1[1]; //转换为相对于左下角的坐标
            result2[1] = (int) pictureSize.y -result2[1];
            result2[3] = (int) pictureSize.y -result2[3];
            result3[1] = (int) pictureSize.y -result3[1];
            result3[3] = (int) pictureSize.y -result3[3];

            Vector2 basePosition = new Vector2(result1[0], result1[1]); //存储相对于屏幕左下角
            Rect hitBox = new Rect(result2[0], result2[1], result2[2]-result2[0], result2[3]-result2[1]); 
            Rect attackBox = new Rect(result3[0], result3[1], result3[2]-result3[0], result3[3]-result3[1]); 

            CharacterPicturesInformation.characterPicturesInfo.computeIfAbsent(behavior, k -> new ArrayList<>())
                .add(new CharacterPicturesInformation.PictureInformation(pictureSize, basePosition, hitBox, attackBox));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[] parseInts(String line) {
        String[] parts = line.split("\\s+");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        return result;
    }   
}