package com.dhkim.network.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class FirebaseDatabase

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class UserFirebaseDatabase

    @FirebaseDatabase
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): DatabaseReference {
        return Firebase.database.reference
    }

    @UserFirebaseDatabase
    @Provides
    @Singleton
    fun provideUserFirebaseDatabase(): DatabaseReference {
        return Firebase.database.getReference("users")
    }
}