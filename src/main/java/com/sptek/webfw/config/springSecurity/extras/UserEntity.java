package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.config.springSecurity.UserRoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@Entity
@Table(name = "USERS")
//@Data //Entity에서는 @Data를 사용하지 않는것이 좋음 (Setter가 노출되지 않도록)
//@Builder //특정 필드만 처리하기 위해 메소드에 빌더 적용
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    /*
    //Users 테이블에 하나의 Role이 직접 저장되는 방식일때 사용 (n:n 관계로 Role 테이블이 없는 경우)
    @Column
    @Enumerated(EnumType.ORDINAL) //순서의 index가 저장됨 (0 or 1)
    @Enumerated(EnumType.STRING)  //해당 값이 저장됨 ("ROLE_USER" or "ROLE_ADMIN")
    private UserRole userRole;
     */

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_ROLE_MAP", //만들어낼 매핑 테이블 이름
            joinColumns = @JoinColumn(name = "user_id"), //각각의 테이블에서 사용될 조인 컬럼
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRoleEntity> userRoleEntitySet; // 유저의 역할

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_TERMS_AGREEMENT_MAP", //만들어낼 매핑 테이블 이름
            joinColumns = @JoinColumn(name = "user_id"), //각각의 테이블에서 사용될 조인 컬럼
            inverseJoinColumns = @JoinColumn(name = "terms_id")
    )
    private Set<TermsAgreementEntity> termsAgreementEntitySet; // 유저의 역할


}
