package com.hecorat.azplugin2.preview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.view.Surface;

/**
 * Created by bkmsx on 2/14/2017.
 */

public class CustomVideoView extends GLSurfaceView implements CustomRenderer.OnSurfaceTextureListener {
    CustomRenderer customRenderer;
    MediaPlayer mediaPlayer;
    Surface surface;
    Context context;

    public CustomVideoView(Context context) {
        super(context);
        this.context = context;
        setEGLContextClientVersion(2);
        customRenderer = new CustomRenderer(this);
        setRenderer(customRenderer);
    }

    @Override
    public void onSurfaceTextureCreated(SurfaceTexture surfaceTexture) {
        surface = new Surface(surfaceTexture);
    }

    public void setVideoSize(float left, float right, float bottom, float top) {
        customRenderer.setupVideoSize(left, right, bottom, top);
    }

    public void setVideoPath(String videoPath) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            customRenderer.stop();
        }
        mediaPlayer = MediaPlayer.create(context, Uri.parse(videoPath));
        mediaPlayer.setSurface(surface);
        customRenderer.reset();
    }

    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public void seekTo(int value) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(value);
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        customRenderer.stop();
    }
}
