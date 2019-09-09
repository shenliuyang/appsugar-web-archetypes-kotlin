package org.appsugar.archetypes.facade

import kotlinx.coroutines.future.await
import org.apache.dubbo.config.annotation.Service
import org.appsugar.archetypes.entity.User
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.util.future
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CompletableFuture

/**
 * 用户对外暴露接口
 */
interface UserFacade {
    fun getByLoginName(loginName: String): CompletableFuture<User?>
}

@Service(version = "1")
class UserFacadeImpl : UserFacade {
    @Autowired
    lateinit var userRepository: UserRepository

    override fun getByLoginName(loginName: String) = future {
        userRepository.findByLoginName(loginName).await()
    }

}