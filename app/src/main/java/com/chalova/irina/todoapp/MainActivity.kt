package com.chalova.irina.todoapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.chalova.irina.todoapp.config.AppConfig.DRAWER_GRAVITY
import com.chalova.irina.todoapp.databinding.MainDrawerHeaderBinding
import com.chalova.irina.todoapp.di.ActivityComponent
import com.chalova.irina.todoapp.login.ui.AuthEvent
import com.chalova.irina.todoapp.login.ui.AuthViewModel
import com.chalova.irina.todoapp.login.ui.LoginStatus
import com.chalova.irina.todoapp.reminder_work.ReminderWorker
import com.chalova.irina.todoapp.reminder_work.ReminderWorker.Companion.NOTIFICATION_WORK
import com.chalova.irina.todoapp.user_profile.ui.UserProfileFragmentDirections
import com.chalova.irina.todoapp.utils.longToast
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity: AppCompatActivity()
{

    lateinit var activityComponent: ActivityComponent
        private set

    private lateinit var navController: NavController
    private lateinit var drawerNavView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var drawer: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var signOutItem: MenuItem? = null
    private var profileItem: MenuItem? = null

    lateinit var authViewModel: AuthViewModel
    lateinit var drawerViewModel: DrawerViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val notificationPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted ->
            if (isGranted) {
                createNotificationWork()
            } else {
                longToast(getString(R.string.notification_disabled))
                WorkManager.getInstance(applicationContext)
                    .cancelUniqueWork(NOTIFICATION_WORK)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        activityComponent = (applicationContext as ToDoApplication)
            .appComponent
            .activityComponentFactory()
            .create()
        activityComponent.inject(this)

        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
        drawerViewModel = ViewModelProvider(this, viewModelFactory)[DrawerViewModel::class.java]

        setupUI()

        lifecycleScope.launch {
            authViewModel.loginStatus
                .flowWithLifecycle(this@MainActivity.lifecycle, Lifecycle.State.STARTED)
                .collect { loginStatus ->
                    if (loginStatus == LoginStatus.LoggedIn) {
                        startNotificationWork()

                        signOutItem?.isVisible = true
                        profileItem?.isEnabled = true
                    } else {
                        signOutItem?.isVisible = false
                        profileItem?.isEnabled = false
                    }
                }
        }
    }

    private fun startNotificationWork() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            createNotificationWork()
        }
    }

    private fun createNotificationWork() {
        val periodicWork = getPeriodicWork()
        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                NOTIFICATION_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWork)
    }

    private fun getPeriodicWork(): PeriodicWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        return PeriodicWorkRequest
            .Builder(
                ReminderWorker::class.java,
                12, TimeUnit.HOURS
            )

            .setInputData(Data.Builder()
                .putInt(ReminderWorker.NOTIFICATION_ID, 0).build())
            .setConstraints(constraints)
            .addTag(NOTIFICATION_WORK)
            .build()
    }

    private fun setupUI() {
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
                as NavHostFragment
        navController = navHostFragment.navController

        setDrawer()
        setToolbar()
    }

    private fun setDrawer() {
        drawer = findViewById(R.id.drawer_layout)

        appBarConfiguration = AppBarConfiguration(
            navController.graph,
            drawer)

        setDrawerView()
    }

    private fun setDrawerView() {
        drawerNavView = findViewById(R.id.nav_view)
        drawerNavView.setupWithNavController(navController)
        val navViewHeaderBinding = MainDrawerHeaderBinding.inflate(layoutInflater, drawerNavView, true)
        navViewHeaderBinding.lifecycleOwner = this
        navViewHeaderBinding.viewmodel = drawerViewModel

        signOutItem = drawerNavView.menu.findItem(R.id.nav_sign_out).apply {
            setOnMenuItemClickListener {
                drawer.closeDrawer(DRAWER_GRAVITY)
                clearBackStackAndNavigateTo(R.id.tasksFragment)
                authViewModel.onAuthEvent(AuthEvent.Logout)

                true
            }
        }
        profileItem = drawerNavView.menu.findItem(R.id.nav_user_profile).apply {
            setOnMenuItemClickListener {
                navController.navigate(
                    UserProfileFragmentDirections.actionGlobalUserProfileFragment(
                        authViewModel.authState.value.userId!!
                    )
                )
                drawer.closeDrawer(DRAWER_GRAVITY)
                true
            }
        }
    }

    private fun setToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun clearBackStackAndNavigateTo(id: Int) {
        val startDestination = navController.graph.startDestinationId
        navController.popBackStack(startDestination, false)
    }

}