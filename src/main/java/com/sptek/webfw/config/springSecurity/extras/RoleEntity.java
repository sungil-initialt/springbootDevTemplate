package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.config.springSecurity.UserRole;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "ROLES")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(unique = true)
    //@Enumerated(EnumType.ORDINAL) //순서의 index가 저장됨 (0 or 1)
    @Enumerated(EnumType.STRING)  //해당 값이 저장됨 ("ROLE_USER" or "ROLE_ADMIN")
    private UserRole userRole;

}
