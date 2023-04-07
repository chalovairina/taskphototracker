package com.chalova.irina.todoapp.user_profile.ui

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.tasks.ui.utils.NavigationArgs
import com.chalova.irina.todoapp.user_profile.data.repository.User
import com.chalova.irina.todoapp.user_profile.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserProfileViewModel @AssistedInject constructor(
    @Assisted val savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
): ViewModel() {

    @AssistedFactory
    interface UserProfileViewModelFactory {

        fun create(savedStateHandle: SavedStateHandle): UserProfileViewModel
    }

    private val userId = savedStateHandle.getStateFlow<String?>(
    NavigationArgs.USER_ID, null)

    private val _isSaved = MutableStateFlow(false)
    private val _userProfileState = MutableStateFlow(UserProfileState())
    val userProfileState: StateFlow<UserProfileState> = _userProfileState

    private var userDataJob: Job? = null
    init {
        viewModelScope.launch {
            userId.collect { userId ->
                if (userId != null) {
                    userDataJob?.cancel()
                    val userData = userRepository.getUserData(userId)
                        userDataJob = combine(userData, _isSaved) { user, isSaved ->
                            _userProfileState.update {
                                UserProfileState(
                                    userId = userId,
                                    userName = user?.name,
                                    userEmail = user?.email,
                                    imageUri = user?.image?.let { Uri.parse(it) },
                                    isSaved = isSaved
                                )
                            }
                    }.launchIn(viewModelScope)
                }
            }
        }
    }

    fun onEvent(event: UserProfileEvent) {
        when (event) {
            is UserProfileEvent.OnEmailChanged -> {
                _userProfileState.update {
                    it.copy(userEmail = event.email)
                }
            }
            is UserProfileEvent.OnUserNameChanged -> {
                _userProfileState.update {
                    it.copy(userName = event.name)
                }
            }
            is UserProfileEvent.OnImageUriChanged -> {
                _userProfileState.update {
                    it.copy(imageUri = event.uri)
                }
            }
            UserProfileEvent.SaveUserData -> updateUser()
        }
    }

    fun updateUserImageUri(uri: Uri) {
        viewModelScope.launch {
            userProfileState.value.userId?.let { userId ->
                userRepository.updateUserImageUrl(userId, uri.toString())
            }
        }
    }
    fun updateUserName(name: String) {
        viewModelScope.launch {
            userProfileState.value.userId?.let { userId ->
                userRepository.updateUserName(userId, name)
            }
        }
    }
    fun updateUserEmail(email: String) {
        viewModelScope.launch {
            userProfileState.value.userId?.let { userId ->
                userRepository.updateUserEmail(userId, email)
            }
        }
    }

    fun updateUser() {

        viewModelScope.launch {
            userProfileState.value.userId?.let { userId ->
                userRepository.updateUser(
                    User(id = userId,
                    name = userProfileState.value.userName,
                    email = userProfileState.value.userEmail,
                    image = userProfileState.value.imageUri?.toString()
                    )
                )
                _isSaved.update { true }
            }
        }
    }
}

fun provideUserProfileFactory(
    assistedFactory: UserProfileViewModel.UserProfileViewModelFactory,
    savedStateHandle: SavedStateHandle
): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(savedStateHandle) as T
        }
    }