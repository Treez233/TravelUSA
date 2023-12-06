package com.example.travelusa

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import org.w3c.dom.Text
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var userInput : EditText
    private lateinit var confirm : Button
    private lateinit var adView : AdView
    private lateinit var prompt : TextView
    private lateinit var tries : TextView
    private lateinit var map : ImageView
    private lateinit var path : TextView
    private var sessionProgress : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userInput = findViewById(R.id.userInput)
        confirm = findViewById(R.id.confirm_button)
        prompt = findViewById(R.id.prompt)
        tries = findViewById(R.id.tries)
        map = findViewById(R.id.mapImageView)
        path = findViewById(R.id.path)
        game = Game()
        game.startGame()
        prompt.text = "Today, I would like to go from ${game.getStart()} to ${game.getEnd()}"
        tries.text = "Number of Attempts Left: ${game.getTries()}"//need to change to progress bar
        map.setColorFilter(PorterDuffColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.SRC_IN))
        map.invalidate()
        createAd()
    }
    fun createAd(){
        adView = AdView(this)
        var adSize : AdSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
        adView.setAdSize(adSize)

        var adUnitId : String = "ca-app-pub-3940256099942544/6300978111" // "ca-app-pub-2833810717312999/5760072379"
        adView.adUnitId = adUnitId

        var builder : AdRequest.Builder = AdRequest.Builder()
        var request : AdRequest = builder.build()

        var layout : AdView = findViewById(R.id.ad_view)
        layout.addView(adView)

        adView.loadAd(request)
    }
    override fun onPause(){
        if (adView != null){
            adView.pause()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (adView != null){
            adView.resume()
        }
        game
    }
    override fun onDestroy() {
        if (adView != null){
            adView.destroy()
        }
        super.onDestroy()
    }
    fun processInput(valid : Boolean, input : String){
        val spannableStringBuilder = SpannableStringBuilder(path.text)
        val res = "${10-game.getTries()}. $input "
        spannableStringBuilder.append(res)
        val colorSpan = if (valid) {
            ForegroundColorSpan(Color.GREEN)
        } else {
            ForegroundColorSpan(Color.RED)
        }
        spannableStringBuilder.setSpan(colorSpan, spannableStringBuilder.length - res.length, spannableStringBuilder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        path.text = spannableStringBuilder
    }
    fun validateInput(v: View){
        if(userInput.text.toString().matches(Regex("^[A-Z]{2}$"))){
            var input : String = userInput.text.toString()
            var valid : Boolean = game.validate(input)
            tries.text = "Number of Tries Left: ${game.getTries()}"
            processInput(valid, input)
        }else{
            Toast.makeText(this, "Incorrect format, please enter two uppercase letters", Toast.LENGTH_LONG).show()
        }
        if(sessionProgress == game.getRouteLen() || game.getTries() == 0){
            game.gameEnd()
            game.setPref(this@MainActivity)
            var intent : Intent = Intent(this, StatActivity::class.java)
            startActivity( intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
    companion object {
        lateinit var game : Game
    }
}