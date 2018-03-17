package org.appsugar.archetypes.entity


import org.hibernate.annotations.*
import org.hibernate.annotations.Cache
import javax.persistence.*
import javax.persistence.Entity

@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@DynamicUpdate
open class User(
        open var name: String = "",
        @get:Column(unique = true)
        open var loginName: String = "",
        open var password: String = "",
        @get:Column(columnDefinition = "TEXT")
        open var permissions: String = ""
) : IdEntity() {
    @get:Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @get:ManyToMany(fetch = FetchType.LAZY)
    @get:Fetch(FetchMode.SUBSELECT)
    @get:JoinTable(name = "user_role", joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
    open var roles: MutableSet<Role> = mutableSetOf() //  let roles out of toString
}