package org.appsugar.archetypes.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.appsugar.archetypes.domain.QUser;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.domain.UserCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslExtension<User> {

    List<User> findByName(String name);

    default Predicate toPredicate(UserCondition c) {
        BooleanBuilder builder = new BooleanBuilder();
        QUser u = QUser.user;
        String name = c.getName();
        if (StringUtils.isNotBlank(name)) {
            builder.and(ops(c.getNameOps(), u.name, name));
        }
        String loginName = c.getLoginName();
        if (StringUtils.isNoneBlank(loginName)) {
            builder.and(ops(c.getLoginNameOps(), u.loginName, loginName));
        }
        return builder;
    }
}
