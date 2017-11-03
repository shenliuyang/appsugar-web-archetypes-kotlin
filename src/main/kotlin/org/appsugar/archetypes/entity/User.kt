package org.appsugar.archetypes.entity

import java.time.LocalDateTime
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import org.appsugar.bean.entity.LongIdEntity
import javax.persistence.Entity
import javax.persistence.Column

@Entity
data class User(
		var name:String="",
		var loginName:String="",
		var password:String=""
):LongIdEntity()