package com.eliyetyang.piano

import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.eliyetyang.piano.model.Major
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.w3c.dom.Text
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    val mediaPlayer1 = MediaPlayer()
    val mediaPlayer2 = MediaPlayer()
    var currentPlayer = mediaPlayer1
    var preparePlayer = mediaPlayer2
    val major = Major()
    var job: Job? = null
    var keyList = mutableListOf<String>()
    var delay = 1000L
    var curName: String? = null

    var currentKeyIndex = 0

    var timeTemp = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initPlayer(mediaPlayer1)
        initPlayer(mediaPlayer2)

        startBTN.setOnClickListener {
            stopAll()
            delay = timeET.text.toString().toLongOrNull() ?: 1000L
            setKeySet()

            val buffer = StringBuffer()

            if (keyList.size > 8) {
                allSoundTV.text = keyList.subList(0, 8).joinTo(buffer, " , ")
            }

            curName = keyList.get(currentKeyIndex)
            currentKeyIndex++
            val curFd = assets.openFd("${curName}.mp3")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                currentPlayer.setDataSource(curFd)
                currentPlayer.prepare()
            }
        }

        stopBTN.setOnClickListener {
            stopAll()
        }

        randomBTN.setOnClickListener {
            random()
        }

    }

    private fun stopAll() {
        job?.cancel()

        currentPlayer.stop()
        currentPlayer.reset()

        preparePlayer.stop()
        preparePlayer.reset()

        currentKeyIndex = 0
    }

    private fun initPlayer(player: MediaPlayer) {
        player.setOnCompletionListener { mp ->
            currentKeyIndex = 0
            mp.reset()
            statusTV.text = "播放完毕"
            Toast.makeText(this, "播放完毕", Toast.LENGTH_SHORT).show()
        }

        player.setOnInfoListener { mp, what, extra ->

            Log.d("Info", "what = ${what}")
            return@setOnInfoListener false
        }

        player.setOnErrorListener { mp, what, extra ->
            Log.d("Error", "what = ${what}")
            return@setOnErrorListener false
        }

        player.setOnPreparedListener {
            Log.d("Prepared", "mediaPlayer = ${it}")
            if (currentPlayer == it) {
                statusTV.text = "正在播放${curName}"
                startPrepare()
                timeTemp = System.currentTimeMillis()
                it.start()
            }
        }

        player.setOnBufferingUpdateListener { mp, percent ->
            statusTV.text = "percent = ${percent}"
        }
    }

    private fun setKeySet() {
        val startSound = startSoundET.text.toString()
        if (startSound.isNullOrEmpty()) {
            statusTV.text = "请输入起始音"
            return
        }
        var index = major.allSound.indexOf(startSound)
        if (index == -1) {
            statusTV.text = "起始音输入错误"
            return
        }

        val loopCount = repeatET.text.toString().toIntOrNull() ?: 2
        if (index + 16 * loopCount > major.allSound.size) {
            statusTV.text = "八度超出键盘长度"
            return
        }

        val isMajor = R.id.majorRB == maOrMiRG.checkedRadioButtonId

        val stepSetting = if (isMajor) major.majorStep else major.harmonicMinorStep
        val tempList = mutableListOf<String>()
        tempList.clear()
        tempList.add(major.allSound.get(index))
        for (i in 1..loopCount) {
            for (step in stepSetting) {
                index += step
                tempList.add(major.allSound.get(index))
            }
        }

        for (i in 1..loopCount) {
            for (s in stepSetting.size - 1 downTo 0 step 1) {
                index -= stepSetting.get(s)
                tempList.add(major.allSound.get(index))
            }
        }

        keyList.clear()
        if (scaleRG.checkedRadioButtonId == R.id.arpeggiosRB) {

            var aIndex = 0
            keyList.add(tempList.get(aIndex))
            for (i in 1..loopCount) {
                for (step in major.arpeggiosStepInScale) {
                    aIndex += step
                    keyList.add(tempList.get(aIndex))
                }
            }

            for (i in 1..loopCount) {
                for (s in major.arpeggiosStepInScale.size - 1 downTo 0 step 1) {
                    aIndex -= major.arpeggiosStepInScale.get(s)
                    keyList.add(tempList.get(aIndex))
                }
            }
        } else {
            keyList = tempList
        }

        Log.d("Tag", tempList.toString())
    }

    fun startPrepare() {
        job = GlobalScope.launch {
            if (currentKeyIndex < keyList.size) {
                curName = keyList.get(currentKeyIndex)
                currentKeyIndex++

                val prepareFd = assets.openFd("${curName}.mp3")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    preparePlayer.setDataSource(prepareFd)
                    preparePlayer.prepare()
                }

                delay(delay)

                if (currentPlayer.isPlaying) {
                    currentPlayer.stop()
                    currentPlayer.reset()
                }

                val temp = currentPlayer
                currentPlayer = preparePlayer
                preparePlayer = temp

                val timeTemp = System.currentTimeMillis()
                val dTime = timeTemp - this@MainActivity.timeTemp - delay
                Log.d("dTime", "dTime = ${dTime}")
                if (dTime > 0) {
                    delay(dTime)
                }

                statusTV.text = "正在播放${curName}"
                this@MainActivity.timeTemp = System.currentTimeMillis()
                currentPlayer.start()
                startPrepare()
            }


        }
        job?.start()
    }

    fun random() {
        val startSound = major.normalStart.get((Math.random() * 7).toInt())
        startSoundET.setText(startSound)

        val isMajor = Math.random() < 0.5
        (if (isMajor) majorRB else minorRB).isChecked = true

        val text = "${startSound} ${if (isMajor) "大调" else "小调"}"

        if (TextUtils.equals(randomTV.text, text)) {
            random()
        } else {
            randomTV.text = text
        }


    }

    override fun onStop() {
        job?.cancel()
        mediaPlayer1.release()
        super.onStop()
    }
}
