package org.appsugar.archetypes.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity
@Table(name = User.TABLE_NAME)
@FieldNameConstants
public class User implements Serializable {
    public static final String TABLE_NAME = "APPSUGAR_USER";
    public static final String USER_ROLE_TABLE_NAME = "APPSUGAR_USER_ROLE";
    public static final String PERMISSION_SPLIT_CHAR = ",";
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    private Long id;
    private String name;
    private String loginName;
    private String password;
    private String permissions;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = User.USER_ROLE_TABLE_NAME, joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @ToString.Exclude
    private List<Role> role;
}
