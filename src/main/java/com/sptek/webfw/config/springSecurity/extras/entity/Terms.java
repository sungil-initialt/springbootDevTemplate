package com.sptek.webfw.config.springSecurity.extras.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "userSet") // 서로 참조하고 있어서 toString() 때 재귀호출로 인한 stack overflow 나는것을 방지
@Entity
@Table(name = "TERMS")
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String termsName;

    @ManyToMany(mappedBy = "termsSet")
    private Set<User> userSet;  // User 엔티티와의 다대다 관계

}
