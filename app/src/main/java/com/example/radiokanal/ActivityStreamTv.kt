package com.example.radiokanal

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.radiokanal.databinding.ActivityStreamTvBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

import com.google.android.exoplayer2.trackselection.TrackSelector

import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor

import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource

import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource



class ActivityStreamTv : AppCompatActivity() {

    lateinit var binding: ActivityStreamTvBinding

    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    val URL = "https://alpha.tv.online.tm/hls/ch001_720/index.m3u8"
    val owaz = "http://radio.telecom.tm/owaz.mp3"
    val char = "http://radio.telecom.tm/tarap.mp3"
    val bunny = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStreamTvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // yeni bir instance baslatılması
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this)

        // DataSource içerisini doldurma
        mediaDataSourceFactory =
            DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayerDemo"))


        // media source nesnesine kullanılacak video türüne göre tanımlama ve url koyma islemi
//        val mediaSource = buildMediaSource(Uri.parse(bunny))
        // ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(Uri.parse(URL))

        val mediaSource = buildMediaSource_m3u8(Uri.parse(URL))

        val mediaSource2 = ProgressiveMediaSource
            .Factory(mediaDataSourceFactory, DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(bunny))

        // player'ı hazır hale getirme
        simpleExoPlayer.prepare(mediaSource, false, false)

        // play oynatılmaya hazır olduğunda video oynatma islemi
        simpleExoPlayer.playWhenReady = true

        // loyout dosyasındaki id degeri eslestirme
        binding.playerView.player = simpleExoPlayer

        // player ekranına focuslanma ozelligi
        binding.playerView.requestFocus()




    }

    fun buildMediaSource(uri: Uri): MediaSource? {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(this, "exoplayer-codelab")
        return HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    private fun buildMediaSource_m3u8(uri: Uri): MediaSource? {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(this, "exoplayer-codelab")
        return HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }
}