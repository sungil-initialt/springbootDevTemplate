package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.config.springSecurity.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "userEntitySet") // 서로 참조하고 있어서 toString() 때 stack overflow 나는것을 방지
@Entity
@Table(name = "ROLE")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(unique = true, name = "ROLE_NAME")
    //@Enumerated(EnumType.ORDINAL) //순서의 index가 저장됨 (0 or 1)
    @Enumerated(EnumType.STRING)  //해당 값이 저장됨 ("ROLE_USER" or "ROLE_ADMIN")
    private RoleEnum roleEnum;

    @ManyToMany(mappedBy = "roleEntitySet")
    private Set<UserEntity> userEntitySet;  // User 엔티티와의 다대다 관계

}
