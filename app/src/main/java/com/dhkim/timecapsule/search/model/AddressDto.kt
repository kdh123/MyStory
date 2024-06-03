package com.dhkim.timecapsule.search.model

import com.dhkim.timecapsule.search.domain.Address

data class AddressDto(
    val documents: List<AddressDocument>,
    val meta: AddressMeta
) {
    fun toAddress(): Address {
        val address = if (documents.isNotEmpty()) {
            documents[0].address_name
        } else {
            "알 수 없음"
        }

        return Address(address = address)
    }
}