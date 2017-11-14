package org.appsugar.archetypes.entity


import org.appsugar.bean.convert.StringListConverter
import org.appsugar.bean.entity.LongIdEntity
import javax.persistence.*

@Entity
data class User(
		var name:String="",
		var loginName:String="",
		var password:String="",
		@get:Column(length = 2500)
		@get:Convert(converter = StringListConverter::class)
		var permissions:List<String>?=emptyList()
):LongIdEntity(){
	@get:ManyToMany(fetch = FetchType.LAZY)
	@get:JoinTable(name="user_role",joinColumns = arrayOf(JoinColumn(name = "user_id")), inverseJoinColumns = arrayOf(JoinColumn(name = "role_id")))
	var roles:Set<Role>?=emptySet()
}