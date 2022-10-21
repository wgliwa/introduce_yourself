package com.example.introduce_yourself.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

class UserLink(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserLink>(UserLinks)

    var link by UserLinks.link
    var user by User referencedOn UserLinks.user
}

object UserLinks : IntIdTable("UserLinks") {
    val link = varchar("link",100)
    val user = reference("from", Users, onDelete = ReferenceOption.CASCADE)
}