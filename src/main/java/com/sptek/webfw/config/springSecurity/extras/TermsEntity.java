package com.sptek.webfw.config.springSecurity.extras;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "userEntitySet")
@Entity
@Table(name = "TERMS")
public class TermsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(unique = true)
    private String termsName;

    @ManyToMany(mappedBy = "termsEntitySet")
    private Set<UserEntity> userEntitySet;  // User 엔티티와의 다대다 관계

}
