package com.ghuljr.onehabit.ui.intro.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ghuljr.onehabit.databinding.ActivityRegisterBinding
import dagger.android.support.DaggerAppCompatActivity

//TODO: create presenter contract when confirmation would be required
class RegisterActivity : DaggerAppCompatActivity() {

    private val viewBind by lazy { ActivityRegisterBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBind.root)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, RegisterActivity::class.java)
    }
}