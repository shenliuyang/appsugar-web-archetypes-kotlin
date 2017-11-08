package org.appsugar.archetypes.condition

import org.appsugar.archetypes.extension.then
import java.io.Serializable

data class UserCondition(
    //startLike
    var name:String="",
    //eq
    var loginName:String=""
):Serializable