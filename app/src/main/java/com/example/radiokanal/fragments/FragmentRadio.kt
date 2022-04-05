package com.example.radiokanal.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radiokanal.Playable
import com.example.radiokanal.R
import com.example.radiokanal.adapter.RadioListAdapter
import com.example.radiokanal.databinding.FragmentRadioBinding
import com.example.radiokanal.model.RadioModel
import com.example.radiokanal.service.CreateNotification
import com.example.radiokanal.service.OnClearFromRecentService
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import com.google.android.exoplayer2.upstream.DataSource




class FragmentRadio : Fragment(), Playable{

    private lateinit var binding: FragmentRadioBinding
    private lateinit var adapter : RadioListAdapter
    private var radioChannelList = ArrayList<RadioModel>()
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    val TAG = "MyExoPlayer"

    lateinit var db: FirebaseFirestore
    lateinit var notificationManager: NotificationManager
    var position = 0;
    var isPlaying = false;

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentRadioBinding.inflate(inflater, container, false)
        adapter = RadioListAdapter(radioChannelList)
        db = FirebaseFirestore.getInstance()
        val radio = db.collection("live").document("radio").collection("channels")

        // yeni bir instance baslatılması
        simpleExoPlayer = activity?.applicationContext?.let { ExoPlayerFactory.newSimpleInstance(it) }!!

        mediaDataSourceFactory =
            activity?.applicationContext?.let { DefaultDataSourceFactory(it, Util.getUserAgent(
                activity?.applicationContext!!, "ExoPlayerDemo")) }!!

        // loyout dosyasındaki id degeri eslestirme
        binding.playerView.player = simpleExoPlayer

        GlobalScope.launch(Dispatchers.IO) {
            val channels = radio.get().await().toObjects(RadioModel::class.java)
            for(ch in channels){
                radioChannelList.add(ch)
            }

            withContext(Dispatchers.Main){
                binding.recyclerviewRadio.adapter = adapter
                binding.recyclerviewRadio.layoutManager = LinearLayoutManager(context)
            }
        }

        adapter?.onItemClick = { item ->
            position = radioChannelList.indexOf(item)
            play(item)
        }

        simpleExoPlayer?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean,playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d(TAG, "onPlayerStateChanged: STATE_IDLE")
                    }
                    Player.STATE_BUFFERING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d(TAG, "onPlayerStateChanged: STATE_BUFFERING")
                    }
                    Player.STATE_READY -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d(TAG, "onPlayerStateChanged: STATE_READY")
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            createChannel()
                            isPlaying = true
                            activity?.registerReceiver(broadCastReceiver, IntentFilter("TRACKS_TRACKS"))
                            activity?.startService(Intent(activity?.baseContext,OnClearFromRecentService::class.java))
                        }
                    }
                    Player.STATE_ENDED -> {
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d(TAG, "onPlayerStateChanged: STATE_ENDED")
                    }
                }
            }
        })

        return binding.root
    }

    private fun createChannel() {
        if(Build.VERSION.SDK_INT >= O){
            var channel = NotificationChannel(CreateNotification.CHANNEL_ID,
            "KOD Dev", NotificationManager.IMPORTANCE_LOW)

            notificationManager = activity?.applicationContext?.getSystemService(NotificationManager::class.java)!!
            if(notificationManager != null){
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun play(item: RadioModel){
        val mediaSource = ProgressiveMediaSource
            .Factory(mediaDataSourceFactory, DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(item.url))

        // player'ı hazır hale getirme
        simpleExoPlayer.prepare(mediaSource, false, false)

        // play oynatılmaya hazır olduğunda video oynatma islemi
        simpleExoPlayer.playWhenReady = true

        // player ekranına focuslanma ozelligi
        binding.playerView.requestFocus()

        CreateNotification.createNotification(activity, item, R.drawable.ic_baseline_play_arrow_24, radioChannelList.indexOf(item), radioChannelList.size-1)
    }

    companion object {
        fun newInstance(): FragmentRadio{
            return FragmentRadio()
        }
    }

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.extras?.getString("actionname")) {
                CreateNotification.ACTION_PREVIOUS -> onTrackPrevious()
                CreateNotification.ACTION_PLAY -> {
                    if(isPlaying){
                        onTrackPause()
                    }else{
                        onTrackPlay()
                    }
                }
                CreateNotification.ACTION_NEXT -> onTrackNext()
            }
        }
    }

    override fun onTrackPrevious() {
        position--
        position %= radioChannelList.size
        CreateNotification.createNotification(activity, radioChannelList.get(position),
        R.drawable.exo_icon_pause, position, radioChannelList.size-1)
        binding.recyclerviewRadio.findViewHolderForAdapterPosition(position)?.itemView?.performClick()
    }

    override fun onTrackPlay() {
        CreateNotification.createNotification(activity, radioChannelList.get(position),
            R.drawable.exo_icon_play, position, radioChannelList.size-1)
        play(radioChannelList[position])
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.playbackState
        isPlaying = true
    }

    override fun onTrackPause() {
        CreateNotification.createNotification(activity, radioChannelList.get(position),
            R.drawable.exo_icon_pause, position, radioChannelList.size-1)
        simpleExoPlayer.playWhenReady = false
        simpleExoPlayer.playbackState
        isPlaying = false
    }

    override fun onTrackNext() {
        position++
        position %= radioChannelList.size
        CreateNotification.createNotification(activity, radioChannelList.get(position),
            R.drawable.exo_icon_next, position, radioChannelList.size-1)
        binding.recyclerviewRadio.findViewHolderForAdapterPosition(position)?.itemView?.performClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll()
        }

        activity?.unregisterReceiver(broadCastReceiver)
    }
}