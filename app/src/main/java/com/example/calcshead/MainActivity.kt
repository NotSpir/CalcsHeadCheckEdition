package com.example.calcshead

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.calcshead.databinding.ActivityMainBinding
import java.lang.Exception
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlin.math.roundToInt

lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCorrect.isEnabled = false
        binding.btnWrong.isEnabled = false

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
    }

    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var totalTime = 0.0
    private var averageTime = 0.0
    private var maxTime = 0.0
    private var minTime = 99999.0
    private var time = 0.0

    var wins = 0
    var total = 0
    var isCorrect = true

    fun onClickNext(view: View) {
        binding.txtTask.setBackgroundColor(Color.WHITE)
        isCorrect = true
        val operandsA = arrayOf("*", "/", "-", "+")
        val operand = operandsA.random()
        var a = (10..99).random()
        var b = (10..99).random()
        var c = 0.00
        if (operand == "/")
        {
            c = (a+0.00)/(b+0.00);
        }
        if (operand == "*")
        {
            c = (a+0.00) * (b+0.00);
        }
        if (operand == "+")
        {
            c = a + b + 0.00;
        }
        if (operand == "-")
        {
            c = a - b - 0.00;
        }
        var choice = (1..2).random()
        if (choice == 2) {
           var innacuracy = (-15..15).random()
            c += innacuracy
            isCorrect = false
        }
        binding.txtTask.text = "${a} ${operand} ${b} = ${c}"
        startTimer()
        binding.btnCorrect.isEnabled = true
        binding.btnWrong.isEnabled = true
        binding.btnNext.isEnabled = false
    }

    fun onClickCorrect(view: View) {
        try {
            if (isCorrect){
                wins++
                total++
                binding.txtTask.setBackgroundColor(Color.GREEN);
            }
            else{
                total++
                binding.txtTask.setBackgroundColor(Color.RED);
            }

            resetTimer()
            binding.txtWinRate.text = ("%.2f".format(((wins*1.00)/(total*1.00) * 100.00))).toString() + "%";
            binding.txtCorrect.text = wins.toString()
            binding.txtTotal.text = total.toString()
            binding.txtWrong.text = (total-wins).toString()
            binding.btnNext.isEnabled = true;
            binding.btnCorrect.isEnabled = false;
            binding.btnWrong.isEnabled = false;
        }
        catch (ex: Exception){

        }
    }
    fun oonClickWrong(view: View) {
        try {
            if (!isCorrect){
                wins++
                total++
                binding.txtTask.setBackgroundColor(Color.RED);
            }
            else{
                total++
                binding.txtTask.setBackgroundColor(Color.GREEN);
            }

            resetTimer()
            binding.txtWinRate.text = ("%.2f".format(((wins*1.00)/(total*1.00) * 100.00))).toString() + "%"
            binding.txtCorrect.text = wins.toString()
            binding.txtTotal.text = total.toString()
            binding.txtWrong.text = (total-wins).toString()
            binding.btnNext.isEnabled = true;
            binding.btnCorrect.isEnabled = false;
            binding.btnWrong.isEnabled = false;
        }
        catch (ex: Exception){

        }
    }

    private fun resetTimer()
    {
        stopTimer()
        totalTime += time
        if (time < minTime) {
            minTime = time
            binding.txtMinTime.text = time.toString()
        }
        if (time > maxTime) {
            maxTime = time
            binding.txtMaxTime.text = time.toString()
        }
        averageTime = totalTime / total
        binding.txtAvgTime.text = averageTime.toString()
        time = 0.0
        binding.txtTimer.text = getTimeStringFromDouble(time)
    }

    private fun startStopTimer()
    {
        if(timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer()
    {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        timerStarted = true
    }

    private fun stopTimer()
    {
        stopService(serviceIntent)
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.txtTimer.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String
    {
        val resultInt = time.roundToInt()
        val seconds = resultInt

        return seconds.toString()
    }


}
