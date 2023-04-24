package com.chalova.irina.todoapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.chalova.irina.todoapp.config.AppConfig.DRAWER_GRAVITY
import com.chalova.irina.todoapp.databinding.ActivityMainBinding
import com.chalova.irina.todoapp.databinding.DrawerHeaderBinding
import com.chalova.irina.todoapp.di.activity_scope.ActivityComponent
import com.chalova.irina.todoapp.login_auth.presentation.auth.AuthEvent
import com.chalova.irina.todoapp.login_auth.presentation.auth.AuthViewModel
import com.chalova.irina.todoapp.login_auth.presentation.login.LoginFragmentDirections
import com.chalova.irina.todoapp.login_auth.presentation.login.LoginStatus
import com.chalova.irina.todoapp.reminder_work.ReminderWorker
import com.chalova.irina.todoapp.reminder_work.ReminderWorker.Companion.NOTIFICATION_WORK
import com.chalova.irina.todoapp.tasks.presentation.utils.NavigationArgs.LOGIN_SUCCESSFUL
import com.chalova.irina.todoapp.user_drawer.presentation.DrawerState
import com.chalova.irina.todoapp.user_drawer.presentation.DrawerViewModel
import com.chalova.irina.todoapp.user_profile.presentation.UserProfileFragmentDirections
import com.chalova.irina.todoapp.utils.longToast
import com.chalova.irina.todoapp.utils.repeatOnState
import com.chalova.irina.todoapp.utils.shortToast
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private var _navViewHeaderBinding: DrawerHeaderBinding? = null
    private val navViewHeaderBinding get() = _navViewHeaderBinding!!

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

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
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        WindowCompat.setDecorFitsSystemWindows(window, false)

        authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
        drawerViewModel = ViewModelProvider(this, viewModelFactory)[DrawerViewModel::class.java]

        setupUI()

        setupUserAuthState(savedInstanceState)
    }

    private fun setupUserAuthState(savedInstanceState: Bundle?) {
        this.repeatOnState(Lifecycle.State.STARTED) {
            checkOnLoginStatus(this, savedInstanceState)
            launch {
                authViewModel.userMessage.collect { message ->
                    shortToast(message)
                }
            }
        }
    }

    private fun checkOnLoginStatus(coroutineScope: CoroutineScope, savedInstanceState: Bundle?) {
        coroutineScope.launch {
            authViewModel.authState.collect { authState ->
                Timber.d("new authState $authState")
                when (authState.loginStatus) {
                    LoginStatus.LoggedIn -> {
                        Timber.d("user logged-in, token valid ${authState.isTokenValid}")
                        enableUserFeatures(true)
                        if (authState.isTokenValid) {
                            startNotificationWork()
                        }
                        if (savedInstanceState == null && !authState.isTokenValid) {
                            authViewModel.onAuthEvent(AuthEvent.LastLoginUnknown)
                        }
                    }
                    LoginStatus.LoggedOut -> {
                        enableUserFeatures(false)
                        signOut()
                    }
                    LoginStatus.NotDefined -> {
                        enableUserFeatures(false)
                    }
                }
            }
        }
    }

    private fun startNotificationWork() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
                periodicWork
            )
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

            .setInputData(
                Data.Builder()
                    .putInt(ReminderWorker.NOTIFICATION_ID, 0).build()
            )
            .setConstraints(constraints)
            .addTag(NOTIFICATION_WORK)
            .build()
    }

    private fun enableUserFeatures(enable: Boolean) {
        if (enable) {
            signOutItem?.isVisible = true
            profileItem?.isEnabled = true
        } else {
            signOutItem?.isVisible = false
            profileItem?.isEnabled = false
        }
    }

    private fun setupUI() {
        setupNavigation()
        setDrawer()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
                    as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setDrawer() {
        drawer = binding.drawerLayout

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.tasksFragment,
                R.id.loginFragment
            ),
            drawerLayout = drawer
        )

        setDrawerView()
        setToolbar()
        setDrawerUiState()
    }

    private fun setDrawerUiState() {
        lifecycleScope.launch {
            drawerViewModel.drawerState.flowWithLifecycle(
                lifecycle, Lifecycle.State.STARTED
            ).collect { state ->
                setupUserImage(state)
                setupEmail(state)
            }
        }
    }

    private fun setupEmail(state: DrawerState) {
        navViewHeaderBinding.userEmailTextView.text = state.userEmail
            ?: getString(R.string.drawer_my_account)
    }

    private fun setupUserImage(state: DrawerState) {
        state.imageUri?.let { uri ->
            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide
                .with(navViewHeaderBinding.userCircleView.context)
                .load(uri)
                .apply(requestOptions)
                .error(R.drawable.ic_user_placeholder)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(navViewHeaderBinding.userCircleView)
        } ?: run {
            navViewHeaderBinding.userCircleView.setImageResource(R.drawable.ic_user_placeholder)
        }
    }

    private fun setDrawerView() {
        drawerNavView = binding.navView
        drawerNavView.setupWithNavController(navController)

        _navViewHeaderBinding = DrawerHeaderBinding.inflate(layoutInflater, drawerNavView, true)

        signOutItem = drawerNavView.menu.findItem(R.id.nav_sign_out).apply {
            setOnMenuItemClickListener {
                drawer.closeDrawer(DRAWER_GRAVITY)
                authViewModel.onAuthEvent(AuthEvent.Logout)

                true
            }
        }
        profileItem = drawerNavView.menu.findItem(R.id.nav_user_profile).apply {
            setOnMenuItemClickListener {
                drawer.closeDrawer(DRAWER_GRAVITY)
                navController.navigate(
                    UserProfileFragmentDirections.actionGlobalUserProfileFragment()
                )

                true
            }
        }
    }

    private fun setToolbar() {
        toolbar = findViewById(R.id.toolbar)
//        toolbar.applyInsetter {
//            type(navigationBars = true, statusBars = true) {
//                padding(horizontal = true, top = true)
//            }
//        }
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun signOut() {
        navController.navigate(LoginFragmentDirections.actionGlobalLoginFragment())
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean(
            LOGIN_SUCCESSFUL,
            authViewModel.authState.value.loginStatus == LoginStatus.LoggedIn
        )
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _navViewHeaderBinding = null
        _binding = null
    }
}