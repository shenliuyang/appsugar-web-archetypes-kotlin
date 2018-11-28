package org.appsugar.archetypes.entity


import org.appsugar.archetypes.repository.hibernate.StringListConverter
import org.hibernate.annotations.*
import org.hibernate.annotations.Cache
import java.util.*
import javax.persistence.*
import javax.persistence.Entity

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@DynamicUpdate
data class User(
        @get:Id
        @get:GeneratedValue(strategy = GenerationType.IDENTITY)
        override var id: Long = 0L,
        override var createdAt: Date = Date(),
        override var updatedAt: Date = Date(),
        var name: String = "",
        @get:Column(unique = true)
        var loginName: String = "",
        var password: String = "",
        @get:Convert(converter = StringListConverter::class)
        @get:Column(columnDefinition = "TEXT")
        var permissions: MutableList<String> = mutableListOf()
) : IdEntityable<Long> {
    @get:Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @get:ManyToMany(fetch = FetchType.LAZY)
    @get:Fetch(FetchMode.SUBSELECT)
    @get:JoinTable(name = "user_role", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
    var roles: MutableSet<Role> = mutableSetOf() //  let roles out of toString
}