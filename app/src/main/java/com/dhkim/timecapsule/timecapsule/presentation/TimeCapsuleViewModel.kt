package com.dhkim.timecapsule.timecapsule.presentation

import androidx.lifecycle.ViewModel
import com.dhkim.timecapsule.timecapsule.domain.TimeCapsuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimeCapsuleViewModel @Inject constructor(
    private val timeCapsuleRepository: TimeCapsuleRepository
) : ViewModel() {



}