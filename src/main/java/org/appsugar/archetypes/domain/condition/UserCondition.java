package org.appsugar.archetypes.domain.condition;

import lombok.Data;

/**
 * 用户查询条件
 */
@Data
public class UserCondition {
    /**
     * 名称前值匹配
     **/
    private String name;
    /**
     * 登录名全文匹配
     **/
    private String loginName;
}
