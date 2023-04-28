package com.chalova.irina.todoapp.user_profile.presentation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chalova.irina.todoapp.R
import com.chalova.irina.todoapp.user_profile.domain.UserProfileUseCases
import com.chalova.irina.todoapp.utils.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserProfileViewModel @AssistedInject constructor(
    @Assisted val savedStateHandle: SavedStateHandle,
    private val userProfileUseCases: UserProfileUseCases
) : ViewModel() {

    @AssistedFactory
    interface UserProfileViewModelFactory {

        fun create(savedStateHandle: SavedStateHandle): UserProfileViewModel
    }

    private val _isSaved = MutableStateFlow(false)
    private val _userName: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _userEmail: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _userImageUri: MutableStateFlow<String?> = MutableStateFlow(null)
    private val userProfile = userProfileUseCases.getUserProfile()
    val userProfileState =
        combine(
            userProfile, _userName, _userEmail, _userImageUri, _isSaved
        ) { user, name, email, uri, isSaved ->
            UserProfileState(
                userName = if (!name.isNullOrEmpty()) name else user?.name,
                userEmail = if (!email.isNullOrEmpty()) email else user?.email,
                imageUri = if (!uri.isNullOrEmpty()) Uri.parse(uri) else user?.image?.let {
                    Uri.parse(
                        it
                    )
                },
                isSaved = isSaved
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UserProfileState()
        )

    private val _userMessage = MutableSharedFlow<Int>()
    val userMessage: SharedFlow<Int> = _userMessage

    fun onEvent(event: UserProfileEvent) {
        when (event) {
            is UserProfileEvent.EmailChanged -> {
                _userEmail.update {
                    event.email
                }
            }
            is UserProfileEvent.UserNameChanged -> {
                _userName.update {
                    event.name
                }
            }
            is UserProfileEvent.ImageUriChanged -> {
                _userImageUri.update {
                    event.uri.toString()
                }
            }
            is UserProfileEvent.SaveUserData -> updateUser()
        }
    }

    private fun updateUser() {
        viewModelScope.launch {
            val result = userProfileUseCases.updateUserProfile(
                userName = userProfileState.value.userName,
                userEmail = userProfileState.value.userEmail,
                userImageUri = userProfileState.value.imageUri?.toString()
            )
            when (result) {
                is Result.Success -> _isSaved.update { true }
                is Result.Error -> _userMessage.emit(R.string.user_unknown_error)
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