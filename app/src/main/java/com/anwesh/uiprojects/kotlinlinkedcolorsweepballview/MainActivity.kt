package com.anwesh.uiprojects.kotlinlinkedcolorsweepballview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.linkedcolorsweepballview.LinkedColorSweepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LinkedColorSweepView.create(this)
    }
}
