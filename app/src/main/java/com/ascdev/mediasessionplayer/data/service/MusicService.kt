package com.ascdev.mediasessionplayer.data.service

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.ascdev.mediasessionplayer.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaSessionService() {

    @Inject
    lateinit var player: ExoPlayer

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Stop playback and shut down the service when the app is swiped away from recents
        player.stop()
        stopSelf()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
