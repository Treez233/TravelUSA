package com.example.travelusa

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TutorialActivity : AppCompatActivity() {
    private lateinit var startButton : Button
    private lateinit var tutorialTextBox : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        startButton = findViewById(R.id.startGame)
        tutorialTextBox = findViewById(R.id.info)
        updateView()
    }

    fun updateView(){
        tutorialTextBox.setTextSize(25.0f)
        tutorialTextBox.setTextColor(Color.WHITE)
        tutorialTextBox.text = "Tutorial\n " +
                "Welcome to the Travel USA game!\n" +
                "In this game, you are provided with a start state and a end state,\n" +
                "You will have 10 chances to determine the shortest path from the start state to the end state.\n" +
                "You can begin by clicking the \"Start\" button when you are ready!\n"
    }

    fun startGame(v: View){
        var intent : Intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // added an animation between tutorial and main activity
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
}