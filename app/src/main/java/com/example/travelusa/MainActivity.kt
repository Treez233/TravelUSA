package com.example.travelusa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class MainActivity : AppCompatActivity() {
    private lateinit var game : Game
    private lateinit var userInput : EditText
    private lateinit var confirm : Button
    private lateinit var adView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userInput = findViewById(R.id.userInput)
        confirm = findViewById(R.id.confirm_button)
        //game = Game()
        createAd()
    }
    fun createAd(){
        adView = AdView(this)
        var adSize : AdSize = AdSize(AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT)
        adView.setAdSize(adSize)

        var adUnitId : String = "ca-app-pub-3940256099942544~3347511713" // "ca-app-pub-2833810717312999/5760072379"
        adView.adUnitId = adUnitId

        var builder : AdRequest.Builder = AdRequest.Builder()
        builder.addKeyword("fitness").addKeyword("workout")
        var request : AdRequest = builder.build()

        var layout : LinearLayout = findViewById(R.id.ad_view)
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
    }
    override fun onDestroy() {
        if (adView != null){
            adView.destroy()
        }
        super.onDestroy()
    }
    fun validateInput(){
        var input : String = userInput.toString()
        game.validate(input)
    }
}