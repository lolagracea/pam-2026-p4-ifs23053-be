package org.delcom.repositories

import org.delcom.dao.DestinationDAO
import org.delcom.entities.Destination
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.DestinationTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import java.util.UUID

class DestinationRepository : IDestinationRepository {

    override suspend fun getDestinations(search: String): List<Destination> =
        suspendTransaction {
            if (search.isBlank()) {
                DestinationDAO.all().map { daoToModel(it) }
            } else {
                val q = "%$search%"

                DestinationDAO.find(
                    SqlExpressionBuilder.run {
                        (DestinationTable.name like q) or
                                (DestinationTable.country like q) or
                                (DestinationTable.city like q)
                    }
                ).map { daoToModel(it) }
            }
        }

    override suspend fun getDestinationById(id: String): Destination? =
        suspendTransaction {
            DestinationDAO.findById(UUID.fromString(id))
                ?.let { daoToModel(it) }
        }

    override suspend fun getDestinationByName(name: String): Destination? =
        suspendTransaction {
            DestinationDAO.find {
                DestinationTable.name eq name
            }.firstOrNull()?.let { daoToModel(it) }
        }

    override suspend fun addDestination(destination: Destination): String =
        suspendTransaction {
            val newDao = DestinationDAO.new(UUID.fromString(destination.id)) {
                this.name = destination.name
                this.country = destination.country
                this.city = destination.city
                this.description = destination.description
                this.pathGambar = destination.pathGambar
                this.createdAt = destination.createdAt
                this.updatedAt = destination.updatedAt
            }
            newDao.id.value.toString()
        }

    override suspend fun updateDestination(id: String, newDestination: Destination): Boolean =
        suspendTransaction {
            val dao = DestinationDAO.findById(UUID.fromString(id))
                ?: return@suspendTransaction false

            dao.apply {
                name = newDestination.name
                country = newDestination.country
                city = newDestination.city
                description = newDestination.description
                pathGambar = newDestination.pathGambar
                updatedAt = newDestination.updatedAt
            }
            true
        }

    override suspend fun removeDestination(id: String): Boolean =
        suspendTransaction {
            val dao = DestinationDAO.findById(UUID.fromString(id))
                ?: return@suspendTransaction false

            dao.delete()
            true
        }
}