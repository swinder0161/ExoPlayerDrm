package com.halil.ozel.exoplayerdrm;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.dash.DashChunkSource;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.dash.DefaultDashChunkSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;

import com.halil.ozel.exoplayerdrm.databinding.ActivityMainBinding;

import java.util.UUID;


/** DRM URL : https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd **/
/** NON DRM URL : https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd **/

@UnstableApi
public class MainActivity extends Activity {

    private ExoPlayer playerView;
    private ActivityMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("swidebug", "*************************************************************************************************");
        Log.i("swidebug", "***********************************Start of new session******************************************");
        Log.i("swidebug", "*************************************************************************************************");
        setView();
        initializePlayer();
    }

    private void setView() {
        binding = ActivityMainBinding.inflate(this.getLayoutInflater());
        ConstraintLayout view = binding.getRoot();
        setContentView(view);
    }

    private void initializePlayer() {
        DefaultHttpDataSource.Factory defaultHttpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(USER_AGENT)
                .setTransferListener(
                        new DefaultBandwidthMeter.Builder(this)
                                .setResetOnNetworkTypeChange(false)
                                .build()
                );

        DashChunkSource.Factory dashChunkSourceFactory =
                new DefaultDashChunkSource.Factory(defaultHttpDataSourceFactory);
        DefaultHttpDataSource.Factory manifestDataSourceFactory =
                new DefaultHttpDataSource.Factory().setUserAgent(USER_AGENT);
        MediaSource dashMediaSource =
                new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
                .createMediaSource(
                        new MediaItem.Builder()
                                .setUri(Uri.parse(URL))
                                // DRM Configuration
                                .setDrmConfiguration(
                                        new MediaItem.DrmConfiguration.Builder(drmSchemeUuid)
                                                .setLicenseUri(DRM_LICENSE_URL).build())
                                .setMimeType(MimeTypes.APPLICATION_MPD)
                                .setTag(null)
                                .build());

        
        // Prepare the player
        playerView = new ExoPlayer.Builder(this)
                .setSeekForwardIncrementMs(10000)
                .setSeekBackIncrementMs(10000)
                .build();
        playerView.setPlayWhenReady(true);
        binding.playerView.setPlayer(playerView);
        playerView.setMediaSource(dashMediaSource, true);
        playerView.prepare();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerView.setPlayWhenReady(false);
    }

    private final String URL =
            "https://bitmovin-a.akamaihd.net/content/art-of-motion_drm/mpds/11331.mpd";
    private final String DRM_LICENSE_URL =
            "https://proxy.uat.widevine.com/proxy?provider=widevine_test";
    private final String USER_AGENT = "ExoPlayer-Drm";
    private final UUID drmSchemeUuid = C.WIDEVINE_UUID; // DRM Type
}