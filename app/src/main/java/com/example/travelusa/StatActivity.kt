package com.example.travelusa

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StatActivity : AppCompatActivity() {
    private lateinit var ratingBar: RatingBar
    private lateinit var stats : TextView
    private lateinit var yes : Button
    private lateinit var no : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stat)
        ratingBar = findViewById(R.id.ratingBar)
        stats = findViewById(R.id.stats)
        yes = findViewById(R.id.yes)
        no = findViewById(R.id.no)
        var handler : ButtonHandler = ButtonHandler()
        yes.setOnClickListener(handler)
        no.setOnClickListener(handler)
        showStats()
        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, _ ->
            Toast.makeText(this, "Thank you for rating us $rating stars!", Toast.LENGTH_LONG).show()
        }
    }
    fun showStats(){
        var win = MainActivity.game.getWin()
        if(win){
            stats.text = "Congratulations! You have won!\n Your current streak is: ${MainActivity.game.getCurrStreak()} \n Your best streak is: ${MainActivity.game.getBestStreak()}"
        }else{
            stats.text = "Better luck next time! Unfortunately, you have lost your streak\n Your best streak is: ${MainActivity.game.getBestStreak()}"
        }
    }
    inner class ButtonHandler : View.OnClickListener{
        override fun onClick(v: View?) {
            when(v?.id){
                R.id.yes -> handleYes()
                R.id.no -> handleNo()
            }
        }
        private fun handleYes(){
            MainActivity.game.startGame()
            finish()
        }
        private fun handleNo(){
            finish()
        }
    }
}
