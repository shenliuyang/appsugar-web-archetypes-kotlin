package org.appsugar.archetypes.entity


import org.appsugar.archetypes.repository.hibernate.StringListConverter
import org.hibernate.annotations.*
import org.hibernate.annotations.Cache
import javax.persistence.*
import javax.persistence.Entity

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@DynamicUpdate
class User(
        var name: String = "",
        @get:Column(unique = true)
        var loginName: String = "",
        var password: String = "",
        @get:Convert(converter = StringListConverter::class)
        @get:Column(columnDefinition = "TEXT")
        var permissions: MutableList<String> = mutableListOf()
) : IdEntity() {
    @get:Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @get:ManyToMany(fetch = FetchType.LAZY)
    @get:Fetch(FetchMode.SUBSELECT)
    @get:JoinTable(name = "user_role", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
    var roles: MutableSet<Role> = mutableSetOf() //  let roles out of toString

    override fun toString(): String {
        return "User(id='$id',name='$name', loginName='$loginName', permissions=$permissions)"
    }

}