package com.dhkim.domain.usecase

import com.dhkim.common.CommonResult
import com.dhkim.domain.model.Address
import com.dhkim.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(lat: String, lng: String): Flow<CommonResult<Address>> {
        return locationRepository.getAddress(lat, lng)
    }
}