package com.dhkim.location

import com.dhkim.location.data.dataSource.remote.LocationApi
import com.dhkim.location.data.model.Address
import com.dhkim.location.data.model.AddressDocument
import com.dhkim.location.data.model.AddressDto
import com.dhkim.location.data.model.AddressMeta
import com.dhkim.location.data.model.PlaceDocument
import com.dhkim.location.data.model.PlaceDto
import com.dhkim.location.data.model.PlaceMeta
import com.dhkim.location.data.model.RoadAddress
import com.dhkim.location.data.model.SameName
import retrofit2.Response
import javax.inject.Inject

class FakeLocationApi @Inject constructor() : com.dhkim.location.data.dataSource.remote.LocationApi {

    private var isError = false

    private val documents = mutableListOf<PlaceDocument>().apply {
        repeat(15) {
            add(
                PlaceDocument(
                    address_name = "서울시 강남구$it",
                    category_group_code = "code$it",
                    category_group_name = "group$it",
                    category_name = "categoryName$it",
                    distance = "$it",
                    id = "placeId$it",
                    phone = "010-1234-1234",
                    place_name = "장소$it",
                    place_url = "url$it",
                    road_address_name = "강남로$it",
                    x = "34.3455",
                    y = "123.4233"
                )
            )
        }
    }

    private val sameName = SameName(
        keyword = "keyword",
        region = listOf(),
        selected_region = "region"
    )

    private val meta = PlaceMeta(
        is_end = false,
        pageable_count = 3,
        same_name = sameName,
        total_count = 15,
    )

    fun setReturnsError() {
        isError = true
    }

    override suspend fun getNearPlaceByKeyword(
        token: String,
        query: String,
        lat: String,
        lng: String,
        range: Int,
        page: Int,
        size: Int
    ): Response<PlaceDto> {
        val placeDto = PlaceDto(
            meta = meta,
            documents = documents
        )

        return if (!isError) {
            Response.success(placeDto)
        } else {
            throw Exception("hello world")
        }
    }

    override suspend fun getPlaceByKeyword(token: String, query: String, page: Int, size: Int): Response<PlaceDto> {
        val placeDto = PlaceDto(
            meta = meta,
            documents = documents
        )

        return if (!isError) {
            Response.success(placeDto)
        } else {
            throw Exception("hello world")
        }
    }

    override suspend fun getPlaceByCategory(
        token: String,
        category: String,
        lat: String,
        lng: String,
        range: Int,
        page: Int,
        size: Int
    ): Response<PlaceDto> {
        val placeDto = PlaceDto(
            meta = meta,
            documents = documents
        )

        return if (!isError) {
            Response.success(placeDto)
        } else {
            throw Exception("hello world")
        }
    }

    override suspend fun getAddress(token: String, lat: String, lng: String): Response<AddressDto> {
        val documents = mutableListOf<AddressDocument>().apply {
            add(
                AddressDocument(
                    address = Address(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    ),
                    road_address = RoadAddress(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                )
            )
        }

        val addressDto = AddressDto(
            documents = documents,
            meta = AddressMeta(10)
        )

        return if (!isError) {
            Response.success(addressDto)
        } else {
            throw Exception("hello world")
        }
    }
}