package org.appsugar.archetypes.repository;

import com.querydsl.core.types.Projections;
import org.appsugar.archetypes.domain.QUser;
import org.appsugar.archetypes.domain.dto.UserStatDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * 用户数据扩展接口
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.repository
 * @className UserRepositoryCustom
 * @date 2021-12-09  13:57
 */
@Repository
public class UserRepositoryCustom extends  BaseJpaRepositoryCustom<QUser>{

    @Autowired
    private EntityManager em;
    @Autowired
    private UserRepository repository;

    public UserRepositoryCustom() {
        super(QUser.user);
    }


    public List<UserStatDto> findUserStat(){
        return createQuery().from(root).leftJoin(root.roles).groupBy(root.id).select(Projections.constructor(UserStatDto.class,root,root.roles.any().count())).fetch();
    }




}
