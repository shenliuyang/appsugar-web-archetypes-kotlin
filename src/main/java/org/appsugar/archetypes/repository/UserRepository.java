package org.appsugar.archetypes.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.appsugar.archetypes.domain.QUser;
import org.appsugar.archetypes.domain.User;
import org.appsugar.archetypes.domain.UserCondition;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface UserRepository extends BaseJpaRepository<User, Long> {

    List<User> findByName(String name);

    //@EntityGraph(attributePaths = "role")
    User findOneByName(String name);

    @Modifying
    @Query("delete from User u where u.name=:name")
    int deleteByName(String name);

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

    static interface NameOnly {
        String getName();

        NameOnly getRole();
    }

}
