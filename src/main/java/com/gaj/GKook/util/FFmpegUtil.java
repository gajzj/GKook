package com.gaj.GKook.util;

import org.bytedeco.javacpp.Loader;

public class FFmpegUtil {
    private static final String FFMPEG;
    private static final String SONG_PATH;
    private static final String SONG_FROM_QQ_PATH;
    private static final String SONG_FROM_MYY_PATH;

    private static Thread ffmpegThread;

    static {
        FFMPEG = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        SONG_PATH = "D:\\bot\\song";
        SONG_FROM_QQ_PATH = "\\qq";
        SONG_FROM_MYY_PATH = "\\myy";
    }

    public static void interrupt() {
        if (ffmpegThread != null) {
            ffmpegThread.interrupt();
        }
    }

    public static String getSongPath(String songName) {

        return null;
    }
//    public static String getSongInfo(String songName) {
//
//    }
//
//    public static String getSongFromLocal(String songName) {
//
//    }


}
