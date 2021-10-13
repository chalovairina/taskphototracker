package com.chari.ic.todoapp.fragments.auth_fragments

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chari.ic.todoapp.MainActivity
import com.chari.ic.todoapp.MainCoroutineRule
import com.chari.ic.todoapp.R
import com.chari.ic.todoapp.data.source.FakeToDoRepository
import com.chari.ic.todoapp.data.source.SignedOutStubDataStoreRepository
import com.chari.ic.todoapp.di.RepositoryModule
import com.chari.ic.todoapp.firebase.MyFireStore
import com.chari.ic.todoapp.repository.Repository
import com.chari.ic.todoapp.repository.datastore.IDataStoreRepository
import com.chari.ic.todoapp.utils.matchesAndroidHome
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class LoginFragmentTest {
    @get: Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    var mainCoroutineRule = MainCoroutineRule()

    @Inject
    lateinit var fakeRepository: FakeToDoRepository
    @ApplicationContext
    private lateinit var context: Context

    private lateinit var documentSnapshot: DocumentSnapshot
    private lateinit var success: Task<AuthResult>
    private lateinit var failure: Task<AuthResult>
    @Mock
    private lateinit var mAuth: FirebaseAuth
    @Mock
    private lateinit var usersFirestore: MyFireStore

    private var logInResult = UNDEF

    companion object {
        private const val SUCCESS = 1
        private const val FAILURE = -1
        private const val UNDEF = 0
    }

    @Before
    fun setUp() {
        // hilt
        hiltRule.inject()

        // mockito
        MockitoAnnotations.openMocks(this)
        success = successTask
        failure = failureTask

        context = ApplicationProvider.getApplicationContext()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        mainCoroutineRule.runBlockingTest {
            fakeRepository.resetRepository()
        }
    }

    // replace repository with fake tasks repo and signedOut dataStore repo
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryTestModule {
        @Singleton
        @Binds
        abstract fun bindToDoRepository(repository: FakeToDoRepository): Repository

        @Singleton
        @Binds
        abstract fun bindDataStoreRepository(dataStoreRepository: SignedOutStubDataStoreRepository): IDataStoreRepository
    }

    @Test
    fun enterAsSignedOut_checkIsDisplayed() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.sign_in_btn_intro)).perform(click())

        onView(withId(R.id.sign_in_btn)).check(matches(isDisplayed()))
    }

    @Test
    fun emptyFields_checkErrorSnackBarShown() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.sign_in_btn_intro)).perform(click())

        onView(withId(R.id.sign_in_email_editText)).perform(replaceText(""))
        onView(withId(R.id.sign_in_password_editText)).perform(replaceText("password"))

        onView(withId(R.id.sign_in_btn)).perform(click())

        onView(withText(R.string.fill_in_email))
            .check(matches(isDisplayed()))

        onView(withId(R.id.sign_in_email_editText)).perform(replaceText("email"))
        onView(withId(R.id.sign_in_password_editText)).perform(replaceText(""))

        onView(withId(R.id.sign_in_btn)).perform(click())

        onView(withText(R.string.fill_in_password))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    // TODO - into large tests
    @Test
    fun fillInFieldsCorrectly_checkPressSignInButtonNavigatesToTasksFragment() {
        val email = "user@mail.ru"
        val password = "123456"
        `when`(mAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(successTask)
        `when`(usersFirestore.loadUser())
            .thenReturn(successUserDocSnapshot)


        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.sign_in_btn_intro)).perform(click())

        onView(withId(R.id.sign_in_email_editText)).perform(replaceText(email))
        onView(withId(R.id.sign_in_password_editText)).perform(replaceText(password))

        onView(withId(R.id.sign_in_btn)).perform(click())

        activityScenario.close()
    }

}