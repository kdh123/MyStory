package com.dhkim.timecapsule.search.data.model

import com.dhkim.timecapsule.search.domain.Address

data class AddressDto(
    val documents: List<AddressDocument>,
    val meta: AddressMeta
) {
    fun toAddress(): Address {
        val address = if (documents.isNotEmpty()) {
            documents[0].road_address.address_name
        } else {
            "알 수 없음"
        }

        val placeName = if (documents.isNotEmpty()) {
            documents[0].road_address.building_name
        } else {
            if (address != "알 수 없음") {
                address
            } else {
                "알 수 없음"
            }
        }

        return Address(address = address, placeName = placeName)
    }
}