package com.imooc.uaa.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imooc.uaa.validation.ValidEmail;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * 用户实体类，实现了 UserDetails 接口
 */
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "mooc_users")
public class User implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 自增长 ID，唯一标识
     */
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     */
    @Getter
    @Setter
    @NotNull
    @Size(max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String username;

    /**
     * 手机号
     */
    @Getter
    @Setter
    @NotNull
    @Pattern(regexp = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$")
    @Size(min = 11, max = 11)
    @Column(length = 11, unique = true, nullable = false)
    private String mobile;

    /**
     * 姓名
     */
    @Getter
    @Setter
    @NotNull
    @Size(max = 50)
    @Column(length = 50)
    private String name;

    /**
     * 是否激活，默认激活
     */
    @Builder.Default
    @Setter
    @NotNull
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 账户是否未过期，默认未过期
     */
    @Builder.Default
    @Setter
    @NotNull
    @Column(name = "account_non_expired", nullable = false)
    private Boolean accountNonExpired = true;

    /**
     * 账户是否未锁定，默认未锁定
     */
    @Builder.Default
    @Setter
    @NotNull
    @Column(name = "account_non_locked", nullable = false)
    private Boolean accountNonLocked = true;

    /**
     * 密码是否未过期，默认未过期
     */
    @Builder.Default
    @Setter
    @NotNull
    @Column(name = "credentials_non_expired", nullable = false)
    private Boolean credentialsNonExpired = true;

    /**
     * 密码哈希
     */
    @Getter
    @Setter
    @JsonIgnore
    @NotNull
    @Size(min = 40, max = 80)
    @Column(name = "password_hash", length = 80, nullable = false)
    private String password;

    /**
     * 电邮地址
     */
    @Getter
    @Setter
    @ValidEmail
    @Size(min = 5, max = 254)
    @Column(length = 254, unique = true, nullable = false)
    private String email;

    /**
     * 角色列表，使用 Set 确保不重复
     */
    @Getter
    @Setter
    @JsonIgnore
    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(
        name = "mooc_users_roles",
        joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
