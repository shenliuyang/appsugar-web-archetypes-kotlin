package org.appsugar.archetypes.web.security

import java.io.Serializable

data class Principal(
        val id:Long=-1,val name:String="anonymous"
) : Serializable