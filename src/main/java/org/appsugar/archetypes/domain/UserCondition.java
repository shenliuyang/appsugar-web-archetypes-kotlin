package org.appsugar.archetypes.domain;

import lombok.Data;

/**
 * 用户查询条件
 */
@Data
public class UserCondition {
    private String name;
    private String nameOps = "STARTS_WITH";
    private String loginName;
    private String loginNameOps = "EQ";
}
