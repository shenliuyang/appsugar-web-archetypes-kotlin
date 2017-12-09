package org.appsugar.archetypes.condition

import java.io.Serializable

data class UserCondition(
        //startLike
        var name: String = "",
        //eq
        var loginName: String = ""
) : Serializable