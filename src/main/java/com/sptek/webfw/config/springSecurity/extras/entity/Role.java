package com.sptek.webfw.config.springSecurity.extras.entity;

import com.sptek.webfw.config.springSecurity.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "userSet") // 서로 참조하고 있어서 toString() 때 재귀호출로 인한 stack overflow 나는것을 방지
@Entity
@Table(name = "ROLE")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "ROLE_NAME")
    //@Enumerated(EnumType.ORDINAL) //순서의 index가 저장됨 (0 or 1)
    @Enumerated(EnumType.STRING)  //해당 값이 저장됨 ("ROLE_USER" or "ROLE_ADMIN")
    private RoleEnum roleEnum;

    @ManyToMany(mappedBy = "roleSet")
    private Set<User> userSet;  // User 엔티티와의 다대다 관계

}
