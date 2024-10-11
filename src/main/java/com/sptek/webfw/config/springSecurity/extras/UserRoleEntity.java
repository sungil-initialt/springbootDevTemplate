package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.config.springSecurity.UserRoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "USER_ROLES")
public class UserRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(unique = true)
    //@Enumerated(EnumType.ORDINAL) //순서의 index가 저장됨 (0 or 1)
    @Enumerated(EnumType.STRING)  //해당 값이 저장됨 ("ROLE_USER" or "ROLE_ADMIN")
    private UserRoleEnum userRoleEnum;

    @ManyToMany(mappedBy = "userRoleEntitySet")
    private Set<UserEntity> userEntitySet;  // User 엔티티와의 다대다 관계

}
