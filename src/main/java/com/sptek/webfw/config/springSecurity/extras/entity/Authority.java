package com.sptek.webfw.config.springSecurity.extras.entity;

import com.sptek.webfw.config.springSecurity.AuthorityIfEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

//todo: Entity는 setter를 막는것을 지향하는데 그러면 매번 DTO->Entity 변환을 Mapper를 사용하지 못하고 Builder로 해야하는데 이게 맞을까?
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "AUTHORITY")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private AuthorityIfEnum authority;

    @Column(unique = true)
    private String code;

    @Column(unique = true)
    private String alias;

    private String desc;
    private String status; //해당 권한의 상태(사용함/사용안함 등)

    @ManyToMany(mappedBy = "authorities")
    private List<Role> roles;

    //@PostLoad
    //@PostPersist
    @PrePersist
    @PreUpdate
    private void initializeDerivedFields() {
        //authority 객체를 기준으로 나머지 값들을 저장하기 위해
        this.code = authority.getCode();
        this.alias = authority.getAlias();
        this.desc = authority.getDesc();
    }
}
