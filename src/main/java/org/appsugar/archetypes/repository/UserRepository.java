package org.appsugar.archetypes.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.appsugar.archetypes.domain.QUser;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.domain.UserCondition;

import java.util.List;

public interface UserRepository extends BaseJpaRepository<User, Long> {

    List<User> findByName(String name);

    default Predicate toPredicate(@NonNull UserCondition c) {
        val builder = new BooleanBuilder();
        val u = QUser.user;
        val name = c.getName();
        if (StringUtils.isNotBlank(name)) {
            builder.and(ops(c.getNameOps(), u.name, name));
        }
        val loginName = c.getLoginName();
        if (StringUtils.isNoneBlank(loginName)) {
            builder.and(ops(c.getLoginNameOps(), u.loginName, loginName));
        }
        return builder;
    }
}
