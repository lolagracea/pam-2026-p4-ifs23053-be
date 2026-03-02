package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Destination(
    var id: String = UUID.randomUUID().toString(),

    var name: String,
    var country: String,
    var city: String? = null,
    var description: String? = null,
    var pathGambar: String? = null,

    @Contextual
    val createdAt: Instant = Clock.System.now(),

    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)