package com.ghuljr.onehabit.ui.goal_details

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ghuljr.onehabit.R

class GoalDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_details)
    }

    companion object {

        private const val EXTRA_GOAL_ID = "extra_goal_id"

        fun intent(from: Context, goalId: String) = Intent(from, GoalDetailsActivity::class.java).apply {
            putExtra(EXTRA_GOAL_ID, goalId)
        }
    }
}