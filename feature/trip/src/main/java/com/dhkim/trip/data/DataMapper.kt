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
        startDate = startDate,
        endDate = endDate,
        places = places,
        images = images.map { it.toTripImage() },
        videos = videos.map { it.toTripVideo() },
        isNextTrip = !DateUtil.isAfter(startDate),
        isDomestic = isDomestic
    )
}

fun TripImageDto.toTripImage(): TripImage {
    return TripImage(
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
        videos = videos.map { it.toTripVideoDto() }
    )
}

fun TripImage.toTripImageDto(): TripImageDto {
    return TripImageDto(
        date, memo, address, imageUrl
    )
}

fun TripVideo.toTripVideoDto(): TripVideoDto {
    return TripVideoDto(
        date, memo, address, videoUrl
    )
}