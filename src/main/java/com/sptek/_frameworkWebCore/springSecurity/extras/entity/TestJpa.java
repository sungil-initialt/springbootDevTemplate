package com.sptek._frameworkWebCore.springSecurity.extras.entity;

import jakarta.persistence.*;
import lombok.*;

//todo: Entity는 setter를 막는것을 지향하는데 그러면 매번 DTO->Entity 변환을 Mapper를 사용하지 못하고 Builder로 해야하는데 이게 맞을까?
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "TESTJPA")
public class TestJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String myKey;

    private String myValue;
}
