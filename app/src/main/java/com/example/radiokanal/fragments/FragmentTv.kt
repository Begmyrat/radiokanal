package com.example.radiokanal.fragments

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.view.ancestors
import com.example.radiokanal.R
import com.example.radiokanal.databinding.FragmentRadioBinding
import com.example.radiokanal.databinding.FragmentTvBinding

class FragmentTv : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentTvBinding
    private lateinit var mediaController: MediaController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        /*
        Russian tv:
        dom-kino -> https://s87.glaz.tv:8082/liveg/domkino.stream/chunks.m3u8?nimblesessionid=295081910&wmsAuthSign=c2VydmVyX3RpbWU9My8yNS8yMDIyIDg6MDM6MjUgUE0maGFzaF92YWx1ZT1Vckt6OWk4TW9YTnBGKy81cTNaUWZBPT0mdmFsaWRtaW51dGVzPTIwMA==
        tvc -> https://s83.glaz.tv:8082/liveg/tvc.stream/chunks.m3u8?nimblesessionid=194676099&wmsAuthSign=c2VydmVyX3RpbWU9My8yNS8yMDIyIDc6NDU6MzQgUE0maGFzaF92YWx1ZT16d0dtRTdpcldKYk5PS1BFNnNwc1RBPT0mdmFsaWRtaW51dGVzPTIwMA==
        ntv -> https://s87.glaz.tv:8082/liveg/ntv.stream/chunks.m3u8?nimblesessionid=295082436&wmsAuthSign=c2VydmVyX3RpbWU9My8yNS8yMDIyIDg6MDQ6NTAgUE0maGFzaF92YWx1ZT1jNmp6WWdHSXI3WUo0UWsyVjVCb3pBPT0mdmFsaWRtaW51dGVzPTIwMA==


        Turkish tv:
        atv -> https://trkvz.daioncdn.net/atv/atv_720p.m3u8?st=GihhsDu-jTEElBv5G7yhyw&e=1648283992&sid=494bcmxflpyu&app=d1ce2d40-5256-4550-b02e-e73c185a314e&ce=3
        tv8 ->  https://tv8-tb-live.ercdn.net/tv8-geo/tv8hd_480p.m3u8?e=1648289169&st=fAdgu-0mBqg-93GJuPxFXA
        trt1 -> https://tv-trt1.medya.trt.com.tr/master_720.m3u8
        bein sports haber -> https://dt-live-bc.ercdn.com/bc/beinsportsnews/beinsportsnews.isml/beinsportsnews-audio_tur=128000-video=2600000.m3u8?st=BusltXSVwCckbW5vnPd45A&e=1648329070&userid=ed63877432
        tv 8.5 -> https://59cba4d34b678.streamlock.net/live2/tv8-5.stream/chunklist_w1947148337.m3u8?hash=2c21450f232b525c9716d470da5636f8
        cnn turk -> https://rr1---sn-u0g3uxax3-xnce.googlevideo.com/videoplayback?expire=1648260826&ei=eiI-YtDkNdOB8gPegb7oDw&ip=78.182.70.53&id=X_EWYemclKA.5&itag=248&aitags=133,134,135,136,137,160,242,243,244,247,248,278&source=yt_live_broadcast&requiressl=yes&mh=JQ&mm=44,29&mn=sn-u0g3uxax3-xnce,sn-nv47lns6&ms=lva,rdu&mv=u&mvi=1&pl=24&ctier=A&hightc=yes&spc=4ocVC9gPH-atQPgCxfqjYDoi1UFBQDReWZ0KsnE_TQ&vprv=1&live=1&hang=1&noclen=1&mime=video/webm&ns=VaJvXyAqvl5piKKMgiZ9JuwG&gir=yes&mt=1648238509&fvip=3&keepalive=yes&fexp=24001373,24007246&c=WEB_EMBEDDED_PLAYER&n=8Fmbh-9dd_zFhQ&sparams=expire,ei,ip,id,aitags,source,requiressl,ctier,hightc,spc,vprv,live,hang,noclen,mime,ns,gir&sig=AOq0QJ8wRAIgO-FaNk_OCSqnSVL7Gm5Y6di4dM6xlD3LtTAlosSC9zICIDTXDxjyLc9m86NDrwjf8RHkLTTCMhfz0Tcr9PBDC-Jj&lsparams=mh,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRAIgYusMnr8lCOiXrZlAlAFeIhJHA0LtJZ5EBmCTKlRqWYgCIEcJ7rmONcQC4rIX9oF_9Fqkqj9_IVbUfLD7eOGHad8Q&alr=yes&cpn=kVPicrO-cmR9dGQ_&cver=1.20220323.01.00&sq=488011&rn=17&rbuf=27268&pot=GpsBCm5eNEE6KXcrADA6AfbjHzX8IY4U5pS1HP8EjXH1REwlfenQ4DfL8okvM8Bz4WNhb9uBGITZktevaHUUB497s-Zo0ZpkitsiEbQHEzIUzh8ZN-VrUNeIgEfGXgha25Vm5LMZQv82KQUmqm4ELDHY6hIpATwYQQ56iRiNgjryJF-uO7cbEy6rRJ9TbMintIJ-F4qEMlN21GOgLtg=
        dmax -> https://dogus.daioncdn.net/dmax/dmax_480p.m3u8?sid=494c56t7faep&app=5a02c599-d17e-4982-9b04-090934d51af7&ce=3
        fox -> https://foxtv-live-ad.ercdn.net/foxtv/foxtv_480p.m3u8?e=1648289504&st=fcxGhU0ijxKt9b6yToVHog

        Turkmen tv:
        altyn asyr -> https://alpha.tv.online.tm/hls/ch001_720/index.m3u8
        yashlyk -> https://alpha.tv.online.tm/hls/ch002_720/index.m3u8
        miras -> https://alpha.tv.online.tm/hls/ch003_720/index.m3u8
        tv4 -> https://alpha.tv.online.tm/hls/ch007_720/index.m3u8
        turkmen owaz -> https://alpha.tv.online.tm/hls/ch005_720/index.m3u8
        turkmen sport -> https://alpha.tv.online.tm/hls/ch006_720/index.m3u8
         */

        binding = FragmentTvBinding.inflate(inflater, container, false)
        binding.videoview.setVideoURI(Uri.parse("https://content.turkmentv.gov.tm/uploads/videos/DokFilm/History.mp4"))
        binding.videoview.requestFocus()
        binding.videoview.setOnPreparedListener{
            binding.videoview.start()
        }

        mediaController = MediaController(activity)
        mediaController.setAnchorView(binding.videoview)
        binding.videoview.setMediaController(mediaController)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    companion object {
        fun newInstance(): FragmentTv{
            return FragmentTv()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {

        }
    }
}