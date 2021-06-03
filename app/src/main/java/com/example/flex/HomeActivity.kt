package com.example.flex

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.flex.Tasks.Task
import com.example.flex.UserTaskView.UserTaskAdapter
import com.example.flex.UserTaskView.UserTaskDetailsFragment
import com.example.flex.UserTaskView.UserTaskRecyclerTouchCallback
import com.example.flex.nav_menu_fragments.BagFragment
import com.example.flex.nav_menu_fragments.ProfileFragment
import com.example.flex.nav_menu_fragments.TasksFragment
import com.example.flex.notify.NotificationService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.romainpiel.shimmer.Shimmer
import com.romainpiel.shimmer.ShimmerTextView
import com.romainpiel.shimmer.ShimmerViewBase
import kotlinx.android.synthetic.main.fragment_user_tasks.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class HomeActivity  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val tv1 = findViewById<TextView>(R.id.tv1)
        val tv2 = findViewById<TextView>(R.id.tv2)
        val shader1 = LinearGradient(0f, 0f, 0f, tv1.textSize, Color.RED, Color.BLUE, Shader.TileMode.CLAMP)
        tv1.paint.shader = shader1
        val shader2 = LinearGradient(0f, 0f, 0f, tv2.textSize, Color.RED, Color.BLUE, Shader.TileMode.CLAMP)
        tv2.paint.shader = shader2
        var title = findViewById<ShimmerTextView>(R.id.title_app)
        var shimmer = Shimmer()
        shimmer.start(title)
        val bar_Graph = findViewById<View>(R.id.bar_graph) as GraphView
        //graph.visibility = View.VISIBLE
        val barGraph_Data: BarGraphSeries<DataPoint> = BarGraphSeries(
            arrayOf(
                DataPoint(0.00, 0.00),
                DataPoint(1.00, LoginActivity.counter_TODO.toDouble()),
                DataPoint(2.00, LoginActivity.counter_MISSED.toDouble()),
                DataPoint(3.00, 0.00),
                DataPoint(4.00, 0.00)
            )
        )
        bar_Graph.addSeries(barGraph_Data)
        barGraph_Data.setValueDependentColor { info ->
            Color.rgb(
                info.x.toInt()  * 255 / 4,
                abs(info.y * 255 / 6).toInt(),
                100
            )
        }
        barGraph_Data.spacing = 20
        //barGraph_Data.isDrawValuesOnTop = true
        //barGraph_Data.valuesOnTopColor = Color.RED


        val bottomNavigationView =
            findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        createNotificationChannel()

        // to fix for professional use ( later on just small design mistake )
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

                when (item.itemId) {
                    R.id.action_bag -> {
                        if (UserTaskDetailsFragment.Opened == false) {
                            closeAllFrags()
                            Toast.makeText(
                                applicationContext,
                                "Bag",
                                Toast.LENGTH_SHORT
                            ).show()
                            showFragmentByTag(BagFragment.TAG)
                        }
                    }

                    R.id.action_home -> {
                        if (UserTaskDetailsFragment.Opened == false) {
                            closeAllFrags()
                            Toast.makeText(
                                applicationContext,
                                "Home",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                        R.id.action_tasks -> {
                            if (UserTaskDetailsFragment.Opened == false) {
                                closeAllFrags()
                            Toast.makeText(
                                applicationContext,
                                "Tasks",
                                Toast.LENGTH_SHORT
                            ).show()
                            showFragmentByTag(TasksFragment.TAG)
                             }

                        }

                        R.id.action_profile -> {
                            closeAllFrags()
                            if (UserTaskDetailsFragment.Opened == false) {
                            Toast.makeText(
                                applicationContext,
                                "Profile",
                                Toast.LENGTH_SHORT
                            ).show()
                            showFragmentByTag(ProfileFragment.TAG)
                            }
                        }

                }

            true
        }
        bottomNavigationView.menu.getItem(1).isChecked = true
    }
    private fun closeAllFrags()
    {
        val all_frags: List<Fragment> = supportFragmentManager.fragments
        if (all_frags.isNotEmpty()){
            for (frag in all_frags) {
                supportFragmentManager.beginTransaction().remove(frag!!).commit()
            }
        }
    }

    private fun showFragmentByTag(tag: String) {
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            if (ProfileFragment.TAG == tag) {
                fragment = ProfileFragment()
            } else if (TasksFragment.TAG == tag) {
                fragment = TasksFragment()
            } else if (BagFragment.TAG == tag) {
                fragment = BagFragment()
            }
        }

        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.fragmentContainer, fragment, tag)

            ft.addToBackStack(null) // add fragment transaction to the back stack

            ft.commit()
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            UserTaskDetailsFragment.Opened = false
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onStop() {
        if (LoginActivity.counter_MISSED != 0 || LoginActivity.counter_TODO != 0)
            startService(Intent(this, NotificationService::class.java))

        super.onStop()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("alert-task", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}