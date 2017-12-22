package org.appsugar.archetypes.entity


import org.hibernate.annotations.*
import org.hibernate.annotations.Cache
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.Entity

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@DynamicUpdate
data class User(
        @get:Id
        @get:GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = Long.MIN_VALUE,
        var name: String = "",
        @get:Column(unique = true)
        var loginName: String = "",
        var password: String = "",
        @get:Column(columnDefinition = "TEXT")
        var permissions: String = "",
        var createdAt: LocalDateTime = LocalDateTime.now(),
        var updatedAt: LocalDateTime = LocalDateTime.now()
) : Serializable {
    @get:ManyToMany(fetch = FetchType.LAZY)
    @get:Fetch(FetchMode.SUBSELECT)
    @get:JoinTable(name = "user_role", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
    var roles: MutableSet<Role> = mutableSetOf() //  let roles out of toString
}