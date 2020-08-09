package org.appsugar.archetypes.repository.kjdbc;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserJdbcEntityRepository extends PagingAndSortingRepository<UserJdbcEntity, Long> {
    UserJdbcEntity findByName(String name);
}