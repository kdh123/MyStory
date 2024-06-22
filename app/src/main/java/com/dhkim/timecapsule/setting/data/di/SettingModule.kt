package com.dhkim.timecapsule.setting.data.di

import com.dhkim.timecapsule.setting.data.repository.SettingRepositoryImpl
import com.dhkim.timecapsule.setting.domain.SettingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingModule {

    @Binds
    @Singleton
    abstract fun bindSettingRepository(settingRepositoryImpl: SettingRepositoryImpl): SettingRepository
}