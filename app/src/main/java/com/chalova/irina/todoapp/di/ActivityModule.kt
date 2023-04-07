package com.chalova.irina.todoapp.di

import dagger.Module

@Module(subcomponents = [
        TasksFragmentComponent::class,
        AddEditFragmentComponent::class,
        UserProfileFragmentComponent::class,
        BottomCalendarFragmentComponent::class])
object ActivityModule