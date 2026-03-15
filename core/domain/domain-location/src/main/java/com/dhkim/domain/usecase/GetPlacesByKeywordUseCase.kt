package com.dhkim.domain.usecase

import androidx.paging.PagingData
import com.dhkim.domain.model.Place
import com.dhkim.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlacesByKeywordUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(query: String): Flow<PagingData<Place>> {
        return locationRepository.getPlaceByKeyword(query = query)
    }
}