package com.dhu777.tagalbum.util;

/**
 * 用以分析图片类型的工具类
 */
public class AlbumItemInfo {
    public static final int MIME_TYPE_UNKNOWN = -1;
    public static final int MIME_TYPE_PICTURE = 0;
    public static final int MIME_TYPE_GIF = 1;
    private static final String[] pictureMimeTypes = {"image/*", "image/jpeg", "image/jpg", "image/png", "image/bmp"};
    private static final String gifMimeType = "image/gif";

    public static int getMimeType(String mimeType){
        if(mimeType.endsWith(gifMimeType))
            return MIME_TYPE_GIF;
        for (String picMimeType:pictureMimeTypes){
            if (mimeType.endsWith(picMimeType))
                return MIME_TYPE_PICTURE;
        }
        return MIME_TYPE_UNKNOWN;
    }
}
