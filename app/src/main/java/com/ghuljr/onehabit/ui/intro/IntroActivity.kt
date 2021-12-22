package com.ghuljr.onehabit.ui.intro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ghuljr.onehabit.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private val viewBind by lazy { ActivityIntroBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBind.root)
    }
}