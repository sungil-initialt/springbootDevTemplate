package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.config.springSecurity.UserRole;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@Getter
@ToString
@Entity
@NoArgsConstructor
//@Data //Entity에서는 @Data를 사용하지 않는것이 좋음 (Setter가 노출되지 않도록)
//@Builder //특정 필드만 처리하기 위해 메소드에 빌더 적용
@Table(name = "USERS")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    //@Enumerated(EnumType.ORDINAL) //순서의 index가 저장됨 (0 or 1)
    @Enumerated(EnumType.STRING)  //해당 값이 저장됨 ("ROLE_USER" or "ROLE_ADMIN")
    private UserRole userRole;

    @Builder //특정 필드만 적용하기 위해
    private UserEntity(String email, String password, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }
}
