package org.delcom.dao

import org.delcom.tables.DestinationTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class DestinationDAO(id: EntityID<UUID>) : Entity<UUID>(id) {

    companion object : EntityClass<UUID, DestinationDAO>(DestinationTable)

    var name by DestinationTable.name
    var country by DestinationTable.country
    var city by DestinationTable.city
    var description by DestinationTable.description
    var pathGambar by DestinationTable.pathGambar
    var createdAt by DestinationTable.createdAt
    var updatedAt by DestinationTable.updatedAt
}