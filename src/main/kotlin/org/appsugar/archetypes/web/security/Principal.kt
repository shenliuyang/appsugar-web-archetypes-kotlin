package org.appsugar.archetypes.web.security

import org.appsugar.archetypes.entity.User
import java.io.Serializable

data class Principal(
        val id:Long=-1,val name:String="anonymous",
        @Transient
        val user:User?=null
) : Serializable