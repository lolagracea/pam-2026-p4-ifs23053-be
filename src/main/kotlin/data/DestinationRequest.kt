package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Destination

@Serializable
data class DestinationRequest(
    var name: String = "",
    var country: String = "",
    var city: String? = null,
    var description: String? = null,
    var pathGambar: String? = null,
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "country" to country,
            "city" to city,
            "description" to description,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Destination {
        return Destination(
            name = name,
            country = country,
            city = city,
            description = description,
            pathGambar = pathGambar
        )
    }
}