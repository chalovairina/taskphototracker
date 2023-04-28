package com.chalova.irina.taskphototracker.user_profile.domain

import com.chalova.irina.taskphototracker.login_auth.domain.GetUserId
import com.chalova.irina.taskphototracker.user_profile.data.repository.User
import com.chalova.irina.taskphototracker.user_profile.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class GetUserProfileImpl(
    private val externalScope: CoroutineScope,
    private val getUserId: GetUserId,
    private val userRepository: UserRepository
) : GetUserProfile {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<User?> {
        return getUserId().flatMapLatest { userId ->
            if (userId != null) {
                userRepository.getUserStream(userId)
            } else {
                flow { }
            }
        }
            .stateIn(
                externalScope,
                SharingStarted.WhileSubscribed(5000L),
                initialValue = null
            )
    }
}