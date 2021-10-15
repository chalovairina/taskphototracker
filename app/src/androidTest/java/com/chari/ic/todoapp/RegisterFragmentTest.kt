package com.chari.ic.todoapp

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.chari.ic.todoapp.data.source.FakeToDoRepository
import com.chari.ic.todoapp.di.RepositoryModule
import com.chari.ic.todoapp.repository.Repository
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
import com.chari.ic.todoapp.utils.Constants
import com.chari.ic.todoapp.utils.DataBindingIdlingResource
import com.chari.ic.todoapp.utils.idling_resource.idling_resource_with_callback.RegisterIdlingResource
import com.chari.ic.todoapp.utils.monitorActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RegisterFragmentTest {
    @get: Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
      lateinit var fakeRepository: FakeToDoRepository
    @ApplicationContext
    private lateinit var context: Context

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)

        hiltRule.inject()

        context = ApplicationProvider.getApplicationContext()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
//        runBlockingTest {
//            fakeRepository.resetRepository()
//        }

        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    // replace repository with fake tasks repo and fake dataStore repo
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryTestModule {
        @Singleton
        @Binds
        abstract fun bindToDoRepository(repository: FakeToDoRepository): Repository

        @Singleton
        @Binds
        abstract fun bindDataStoreRepository(dataStoreRepository: FakeDataStoreRepository): IDataStoreRepository
    }

    @Test
    fun registerNewUser_ok() {
        IdlingRegistry.getInstance().register(RegisterIdlingResource)

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.sign_up_btn_intro)).perform(click())

        onView(withId(R.id.sign_up_email_editText)).perform(replaceText("john29@mail.ru"))
        onView(withId(R.id.sign_up_username_editText)).perform(replaceText("JohnLennon"))
        onView(withId(R.id.sign_up_password_editText)).perform(replaceText("pasSwo\$rd1"))

        // Pause dispatcher so you can verify initial values.
//        mainCoroutineRule.pauseDispatcher()

        onView(withId(R.id.sign_up_btn)).perform(click())

//        onView(withText(R.string.please_wait)).check(matches(isDisplayed()))

        // Execute pending coroutines actions.
//        mainCoroutineRule.resumeDispatcher()

            onView(withId(R.id.no_data_imageView)).check(matches(isDisplayed()))

        runOnUiThread {
            FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .delete()
            FirebaseAuth.getInstance().signOut()
        }

        IdlingRegistry.getInstance().unregister(RegisterIdlingResource)

        activityScenario.close()
    }

}