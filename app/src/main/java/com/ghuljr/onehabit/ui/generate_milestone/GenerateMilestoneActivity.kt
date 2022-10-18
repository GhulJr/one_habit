package com.ghuljr.onehabit.ui.generate_milestone

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ghuljr.onehabit.R
import com.ghuljr.onehabit.databinding.ActivityGenerateMilestoneBinding
import com.ghuljr.onehabit.ui.base.BaseActivity
import com.ghuljr.onehabit_presenter.generate_milestone.GenerateMilestonePresenter
import com.ghuljr.onehabit_presenter.generate_milestone.GenerateMilestoneView

class GenerateMilestoneActivity : BaseActivity<ActivityGenerateMilestoneBinding, GenerateMilestoneView, GenerateMilestonePresenter>(), GenerateMilestoneView {

    private val navController by lazy { (supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment).navController }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.toolbar.setupWithNavController(navController)
    }

    override fun bindView(): ActivityGenerateMilestoneBinding = ActivityGenerateMilestoneBinding.inflate(layoutInflater)
    override fun getPresenterView(): GenerateMilestoneView = this


    companion object {
        fun instance(from: Context) = Intent(from, GenerateMilestoneActivity::class.java)
    }
}