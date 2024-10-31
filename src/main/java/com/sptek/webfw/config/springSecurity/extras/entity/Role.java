package com.sptek.webfw.config.springSecurity.extras.entity;

import com.sptek.webfw.config.springSecurity.RoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

//todo: Entity는 setter를 막는것을 지향하는데 그러면 매번 DTO->Entity 변환을 Mapper를 사용하지 못하고 Builder로 해야하는데 이게 맞을까?
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ToString(exclude = "users") // 서로 참조로 인한 toStrig에서의 SOF 방지
@Table(name = "ROLE")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;  // User 엔티티와의 다대다 관계

}
