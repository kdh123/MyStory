package com.dhkim.trip.data

import com.dhkim.common.DateUtil
import com.dhkim.database.TripImageDto
import com.dhkim.database.TripVideoDto
import com.dhkim.database.entity.TripEntity
import com.dhkim.trip.domain.model.Trip
import com.dhkim.trip.domain.model.TripImage
import com.dhkim.trip.domain.model.TripPlace
import com.dhkim.trip.domain.model.TripVideo

fun TripEntity.toTrip(): Trip {
    val isDomestic = places.firstOrNull { place ->
        TripPlace.AbroadPlace.entries.map {
            it.placeName
        }.contains(place)
    } == null

    return Trip(
        id = id,
        type = type,
        startDate = startDate,
        endDate = endDate,
        places = places,
        images = images.map { it.toTripImage() },
        videos = videos.map { it.toTripVideo() },
        isNextTrip = !DateUtil.isAfter(startDate),
        isDomestic = isDomestic,
        isInit = isInit
    )
}

fun TripImageDto.toTripImage(): TripImage {
    return TripImage(
        id = id,
        date = date,
        memo = memo,
        address = address,
        imageUrl = imageUrl
    )
}

fun TripVideoDto.toTripVideo(): TripVideo {
    return TripVideo(
        date = date,
        memo = memo,
        address = address,
        videoUrl = videoUrl
    )
}

fun Trip.toTripEntity(): TripEntity {
    return TripEntity(
        id = id,
        type = type,
        startDate = startDate,
        endDate = endDate,
        places = places,
        images = images.map { it.toTripImageDto() },
        videos = videos.map { it.toTripVideoDto() },
        isInit = isInit
    )
}

fun TripImage.toTripImageDto(): TripImageDto {
    return TripImageDto(
        id, date, memo, lat, lng, address, imageUrl
    )
}

fun TripVideo.toTripVideoDto(): TripVideoDto {
    return TripVideoDto(
        date, memo, lat, lng, address, videoUrl
    )
}