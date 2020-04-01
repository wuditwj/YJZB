package io.agora.tutorials.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;

/**
 * 控制铃声类
 */
public class BellUtils {

    private static MediaPlayer mMediaPlayer;

    public static void startPlay(Context context){
        mMediaPlayer= new MediaPlayer();
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        try {
            mMediaPlayer.setDataSource(context, alert);  //后面的是try 和catch ，自动添加的
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mMediaPlayer.setLooping(true);   //循环播放开
            mMediaPlayer.prepare();    //后面的是try 和catch ，自动添加的
        } catch (IllegalArgumentException | IllegalStateException | SecurityException | IOException e1) {
            e1.printStackTrace();
        }

        mMediaPlayer.start();//开始播放
    }

    public static void stopPlay(){
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
