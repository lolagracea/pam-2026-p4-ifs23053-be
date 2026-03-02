package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object DestinationTable : UUIDTable("destinations") {

    val name = varchar("name", 120)
    val country = varchar("country", 100)
    val city = varchar("city", 100).nullable()

    val description = text("description").nullable()

    val pathGambar = varchar("path_gambar", 255).nullable()

    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}