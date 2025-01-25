package com.dhkim.location.domain.usecase

import androidx.paging.PagingData
import com.dhkim.location.domain.model.Place
import com.dhkim.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNearPlacesByKeywordUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(query: String, lat: String, lng: String): Flow<PagingData<Place>> {
        return locationRepository.getNearPlaceByKeyword(query = query, lat = lat, lng = lng)
    }
}