package com.sptek.webfw.config.springSecurity.extras.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.ibatis.annotations.One;
import org.hibernate.mapping.ToOne;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@Entity
@Table(name = "USER_ADDRESS")
//@Data //Entity에서는 @Data를 사용하지 않는것이 좋음 (Setter가 노출되지 않도록??)
//@Builder //특정 필드만 처리하기 위해 메소드에 빌더 적용
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String addressLabel;
    private String address;

}
