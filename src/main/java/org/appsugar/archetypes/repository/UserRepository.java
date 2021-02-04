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
        val b = new BooleanBuilder();
        val q = QUser.user;
        val name = c.getName();
        if (StringUtils.isNotBlank(name)) {
            b.and(q.name.startsWith(name));
        }
        val loginName = c.getLoginName();
        if (StringUtils.isNoneBlank(loginName)) {
            b.and(q.loginName.eq(loginName));
        }
        return b;
    }
}
