package com.dhkim.location.domain.usecase

import androidx.paging.PagingData
import com.dhkim.location.domain.model.Category
import com.dhkim.location.domain.model.Place
import com.dhkim.location.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlacesByCategoryUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(category: Category, lat: String, lng: String): Flow<PagingData<Place>> {
        return locationRepository.getPlaceByCategory(category = category, lat = lat, lng = lng)
    }
}