package org.appsugar.archetypes.entity


import org.appsugar.bean.convert.StringListConverter
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.OrderBy
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@DynamicUpdate
data class User(
        @get:Id
        @get:GeneratedValue(strategy = GenerationType.IDENTITY)
        var id:Long = Long.MIN_VALUE,
		var name:String="",
		@get:Column(unique = true)
		var loginName:String="",
		var password:String="",
		@get:Column(columnDefinition="TEXT")
		@get:Convert(converter = StringListConverter::class)
		var permissions:MutableList<String> = mutableListOf(),
        var createdAt:Date = Date(),
        var updatedAt:Date = Date()
){
	@get:ManyToMany(fetch = FetchType.LAZY)
	@get:JoinTable(name="user_role",joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
	var roles:MutableSet<Role> = mutableSetOf() //  let roles out of toString
}