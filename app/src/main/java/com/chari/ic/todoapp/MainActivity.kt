package com.chari.ic.todoapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.chari.ic.todoapp.databinding.MainDrawerHeaderBinding
import com.chari.ic.todoapp.firebase.users.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

private const val DRAWER_GRAVITY = GravityCompat.START
@AndroidEntryPoint
class MainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var toolbar: Toolbar
    private lateinit var drawer: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navViewHeader: View

    private var currentUser: User? = null

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val toDoViewModel: ToDoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TODOApp)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        drawer = findViewById(R.id.drawer_layout)
        // tell the activity that these fragments can be 'start' destination meaning to exit on up button pressed
        appBarConfiguration = AppBarConfiguration(
            setOf(
            R.id.tasksFragment,
            R.id.introFragment,
        ), drawer)


        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
        navViewHeader = navView.getHeaderView(0)
        val navViewHeaderBinding = MainDrawerHeaderBinding.inflate(layoutInflater, navView, true)
        navViewHeaderBinding.lifecycleOwner = this
        navViewHeaderBinding.viewmodel = toDoViewModel

        toDoViewModel.currentUser.observe(this) { user ->
            if (user.userId.isNotEmpty() && Firebase.auth.currentUser != null) {
                currentUser = User(
                    user.userId, user.userName, user.userEmail,
                    user.userImageUrl, user.userMobile, user.fcmToken
                )
            } else {
                currentUser = null
                toDoViewModel.writeCurrentUserData("", "", 0L, "", "", "")
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                if (currentUser != null) {
                    navController.navigate(R.id.action_global_userProfileFragment)
                } else {
                    Toast.makeText(this, getString(R.string.register_or_login), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_sign_out -> {
                if (currentUser != null) {
                    signOut()
                } else {
                    Toast.makeText(this, getString(R.string.register_or_login), Toast.LENGTH_SHORT).show()
                }
            }
        }
        drawer.closeDrawer(DRAWER_GRAVITY)

        return true
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        toDoViewModel.writeCurrentUserData("", "", 0L, "", "", "")

        clearBackStackAndNavigateTo(R.id.introFragment)
    }

    private fun clearBackStackAndNavigateTo(destinationId: Int) {
        if (navController.backQueue.isNotEmpty()) {
            val backstack = navController.backQueue
            val backstackBottom = backstack[0]
            val navOptions = NavOptions.Builder()
                .setPopUpTo(backstackBottom.destination.id, true)
                .build()
            navController.navigate(destinationId, null, navOptions)
        }
    }

}