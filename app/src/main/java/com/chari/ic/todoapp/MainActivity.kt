package com.chari.ic.todoapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.chari.ic.todoapp.firebase.users.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

private const val DRAWER_GRAVITY = GravityCompat.START
@AndroidEntryPoint
class MainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var toolbar: Toolbar
    private lateinit var drawer: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navViewHeader: View
    private lateinit var userProfileImage: CircleImageView
    private lateinit var userProfileEmail: TextView

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

        userProfileImage = navViewHeader.findViewById(R.id.user_circle_view)
        userProfileEmail = navViewHeader.findViewById(R.id.user_email_textView)

        fillUserDrawerProfile()
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
        toDoViewModel.writeUserLoggedIn(false)
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

    private fun fillUserDrawerProfile() {
        toDoViewModel.userLoggedIn.asLiveData().observe(this) { loggedIn ->
            if (loggedIn) {
                toDoViewModel.currentUser.asLiveData().observe(this) { user ->
                    currentUser = User(user.userId, user.userName, user.userEmail,
                        user.userImageUrl, user.userMobile, user.fcmToken)
                    Glide
                        .with(this)
                        .load(user.userImageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_placeholder)
                        .into(userProfileImage)

                    userProfileEmail.text = user.userEmail
                }
            } else {
                currentUser = null
                toDoViewModel.currentUser.asLiveData().removeObservers(this)
                userProfileEmail.text = getString(R.string.my_account)
                userProfileImage.setImageResource(R.drawable.ic_user_placeholder)
            }
        }

    }

}