package com.example.travelusa

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class MainActivity : AppCompatActivity() {
    private lateinit var userInput : EditText
    private lateinit var confirm : Button
    private lateinit var adView : AdView
    private lateinit var prompt : TextView
    private lateinit var tries : TextView
    private lateinit var map : ImageView
    private lateinit var path : TextView
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // static variable to track the path

        setContentView(R.layout.activity_main)
        userInput = findViewById(R.id.userInput)
        confirm = findViewById(R.id.confirm_button)
        prompt = findViewById(R.id.prompt)
        tries = findViewById(R.id.tries)
        map = findViewById(R.id.mapImageView)
        path = findViewById(R.id.path)
        progressBar = findViewById(R.id.progress_bar)
        game = Game(this@MainActivity)
        game.startGame()
        var str1 : String = getColoredSpanned(game.getStart(), "#ADD8E6")
        var str2 : String = getColoredSpanned(game.getEnd(), "#FFB6C1")
        prompt.text = Html.fromHtml("Today, I would like to go from " + str1 + " to " + str2, 0, null, null)
        tries.text = "Number of Attempts Left: ${game.getTries()}"
        progressBar.max = game.getRouteLen()
        map.setColorFilter(PorterDuffColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.SRC_IN))
        map.invalidate()
        createAd()
    }

    fun createAd(){
        adView = AdView(this)
        var adSize : AdSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
        adView.setAdSize(adSize)

        var adUnitId : String = "ca-app-pub-3940256099942544/6300978111"
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

        var str1 : String = getColoredSpanned(game.getStart(), "#ADD8E6")
        var str2 : String = getColoredSpanned(game.getEnd(), "#FFB6C1")
        prompt.text = Html.fromHtml("Today, I would like to go from " + str1 + " to " + str2, 0, null, null)
        tries.text = "Number of Attempts Left: ${game.getTries()}"
        path.text = ""
        progressBar.max = game.getRouteLen()
        progressBar.progress = 0
    }

    override fun onDestroy() {
        if (adView != null){
            adView.destroy()
        }
        super.onDestroy()
    }

    fun processInput(valid : Boolean, input : String){
        val spannableStringBuilder = SpannableStringBuilder(path.text)

        Log.w("MainActivity", "processInput(): $spannableStringBuilder")

        val res = "${10-game.getTries()}. $input "

        spannableStringBuilder.append(res)
        val colorSpan = if (valid) {
            ForegroundColorSpan(Color.GREEN)
        } else {
            ForegroundColorSpan(Color.RED)
        }

        spannableStringBuilder.setSpan(colorSpan, spannableStringBuilder.length - res.length, spannableStringBuilder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        path.text = spannableStringBuilder

        // update progress bar if valid
        if (valid) {
            progressBar.progress += 1
        }
    }

    fun validateInput(v: View){
        // capitalizing input and removing leading/trailing whitespace
        var input : String = userInput.text.toString().uppercase().trim()
        var properInput : Boolean = false

        // checking if this input is a valid state (either full name or 2 letter abbreviation)
        if (game.stateAbbreviations.contains(input)) {
            properInput = true
        }

        if (game.contiguousStates.contains(input)) {
            properInput = true
            var idx : Int = game.contiguousStates.indexOf(input)
            // converting valid full state name to 2 letter abbreviation
            input = game.stateAbbreviations.get(idx)
        }

        if(properInput){
            // checking if this state has already been guessed
            if (game.addGuess(input)) {
                var valid : Boolean = game.validate(input)
                tries.text = "Number of Tries Left: ${game.getTries()}"
                processInput(valid, input)
                userInput.text = SpannableStringBuilder("")
            } else {
                Toast.makeText(this, "State already guessed, please enter a different state", Toast.LENGTH_LONG).show()
            }
        } else{
            Toast.makeText(this, "Please enter valid state name or 2-letter abbreviation", Toast.LENGTH_LONG).show()
        }

        if(game.getProgress() == game.getRouteLen() || game.getTries() == 0){
            game.gameEnd()
            game.setPref(this@MainActivity)
            var intent : Intent = Intent(this, StatActivity::class.java)
            startActivity( intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    private fun getColoredSpanned(text: String, color: String): String {
        return "<font color=$color>$text</font>"
    }

    companion object {
        lateinit var game : Game
    }
}