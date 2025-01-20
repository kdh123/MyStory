package com.dhkim.location.domain.usecase

import androidx.paging.PagingData
import com.dhkim.location.domain.model.Place
import com.dhkim.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlacesByKeywordUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(query: String): Flow<PagingData<Place>> {
        return locationRepository.getPlaceByKeyword(query = query)
    }
}