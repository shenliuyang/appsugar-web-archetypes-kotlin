package org.appsugar.archetypes.entity


import org.appsugar.archetypes.entity.conversion.StringListConverter
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDateTime
import javax.persistence.*

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
        @get:Convert(converter = StringListConverter::class)
        var permissions: MutableList<String> = mutableListOf(),
        var createdAt: LocalDateTime = LocalDateTime.now(),
        var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @get:ManyToMany(fetch = FetchType.LAZY)
    @get:Fetch(FetchMode.SUBSELECT)
    @get:JoinTable(name = "user_role", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
    var roles: MutableSet<Role> = mutableSetOf() //  let roles out of toString
}