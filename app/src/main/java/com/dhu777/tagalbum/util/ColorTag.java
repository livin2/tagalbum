package com.dhu777.tagalbum.util;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;

public class ColorTag {
    public static final String DARK = "暗色";
    public static final String LIGHT = "亮色";
    public static final String BLACK = "暗";
    public static final String WHITE = "白";
    public static final String OTHER = "未分类色";
    /**hue 340-15*/
    public static final String RED = "红";
    /**hue 15-40*/
    public static final String ORANGE = "橙";
    /**hue 40-65*/
    public static final String YELLOW = "黄";
    /**hue 65-90*/
    public static final String LIME = "黄绿";
    /**hue 90-150*/
    public static final String GREEN = "绿";
    /**hue 150-185*/
    public static final String CYAN = "青";
    /**hue 185-220*/
    public static final String BLUE = "蓝";
    /**hue 220--255*/
    public static final String INDIGO = "蓝紫";
    /**hue 255-295*/
    public static final String PURPLE = "紫";
    /**hue 295-340*/
    public static final String PINK = "粉";

    public static float DV_RED_ORANGE = 15f;
    public static float DV_ORANGE_YELLOW = 40f;
    public static float DV_YELLOW_LIME = 65f;
    public static float DV_LIME_GREEN = 90f;
    public static float DV_GREEN_CYAN = 150f;
    public static float DV_CYAN_BLUE = 185f;
    public static float DV_BLUE_INDIGO = 220f;
    public static float DV_INDIGO_PURPLE = 255f;
    public static float DV_PURPLE_PINK = 295f;
    public static float DV_PINK_RED = 340f;

    public static String tagOf(@NonNull Palette.Swatch s){
        if(s==null) return OTHER;
        float hev[] = s.getHsl();
        if(hev == null) return OTHER;
        if(isBlack(hev))
            return BLACK;
        if(isWhite(hev))
            return WHITE;
        if(isRed(hev))
            return RED;
        if(isOrange(hev))
            return ORANGE;
        if(isYellow(hev))
            return YELLOW;
        if(isLime(hev))
            return LIME;
        if(isGreen(hev))
            return GREEN;
        if(isCyan(hev))
            return CYAN;
        if(isBlue(hev))
            return BLUE;
        if(isIndigo(hev))
            return INDIGO;
        if(isPurple(hev))
            return PURPLE;
        if(isPink(hev))
            return PINK;
        return OTHER;
    }

    public static boolean isBlack(float hev[]){
        return hev[2]<0.10;
    }
    public static boolean isWhite(float hev[]){
        return hev[2]>0.90 && hev[1]<0.10;
    }

    public static boolean isDark(float hev[]){
        return hev[2]<0.45;
    }
    public static boolean isLight(float hev[]){
        return hev[1]<0.40&&hev[2]>0.85;
    }
    public static boolean isRed(float hev[]){
        //special
        return hev[0]>DV_PINK_RED || hev[0]<=DV_RED_ORANGE;
    }
    public static boolean isOrange(float hev[]){
        return hev[0]>DV_RED_ORANGE && hev[0]<=DV_ORANGE_YELLOW;
    }
    public static boolean isYellow(float hev[]){
        return hev[0]>DV_ORANGE_YELLOW && hev[0]<=DV_YELLOW_LIME;
    }
    public static boolean isLime(float hev[]){
        return hev[0]>DV_YELLOW_LIME && hev[0]<=DV_LIME_GREEN;
    }
    public static boolean isGreen(float hev[]){
        return hev[0]>DV_LIME_GREEN && hev[0]<=DV_GREEN_CYAN;
    }
    public static boolean isCyan(float hev[]){
        return hev[0]>DV_GREEN_CYAN && hev[0]<=DV_CYAN_BLUE;
    }
    public static boolean isBlue(float hev[]){
        return hev[0]>DV_CYAN_BLUE && hev[0]<=DV_BLUE_INDIGO;
    }
    public static boolean isIndigo(float hev[]){
        return hev[0]>DV_BLUE_INDIGO && hev[0]<=DV_INDIGO_PURPLE;
    }
    public static boolean isPurple(float hev[]){
        return hev[0]>DV_INDIGO_PURPLE && hev[0]<=DV_PURPLE_PINK;
    }
    public static boolean isPink(float hev[]){
        return hev[0]>DV_PURPLE_PINK && hev[0]<=DV_PINK_RED;
    }
}
