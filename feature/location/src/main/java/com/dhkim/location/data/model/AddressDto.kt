package com.dhkim.location.data.model

import com.dhkim.location.domain.Address

data class AddressDto(
    val documents: List<AddressDocument>,
    val meta: AddressMeta
) {
    fun toAddress(): Address {
        val standardAddress = if (documents.isNotEmpty()) {
            documents[0].address?.address_name ?: "알 수 없음"
        } else {
            "알 수 없음"
        }

        val address = if (documents.isNotEmpty()) {
            documents[0].road_address?.address_name ?: standardAddress
        } else {
            "알 수 없음"
        }

        val placeName = if (documents.isNotEmpty()) {
            documents[0].road_address?.building_name ?: standardAddress
        } else {
            "알 수 없음"
        }

        return Address(address = address, placeName = placeName)
    }
}