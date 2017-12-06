package org.appsugar.archetypes.entity


import org.appsugar.bean.convert.StringListConverter
import java.util.*
import javax.persistence.*

@Entity
data class User(
        @get:Id
        @get:GeneratedValue(strategy = GenerationType.IDENTITY)
		@get:Column(insertable = false)
        var id:Long = Long.MIN_VALUE,
		var name:String="",
		var loginName:String="",
		var password:String="",
		@get:Column(columnDefinition="TEXT")
		@get:Convert(converter = StringListConverter::class)
		var permissions:List<String> = mutableListOf(),
        var createdAt:Date = Date(),
        var updatedAt:Date = Date()
){
	@get:ManyToMany(fetch = FetchType.LAZY)
	@get:JoinTable(name="user_role",joinColumns = [JoinColumn(name = "user_id")], inverseJoinColumns = [JoinColumn(name = "role_id")])
	var roles:Set<Role> = mutableSetOf() //  let roles out of toString
}