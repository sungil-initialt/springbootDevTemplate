package com.sptek.webfw.config.springSecurity.extras.repository;

import com.sptek.webfw.config.springSecurity.extras.entity.Role;
import com.sptek.webfw.config.springSecurity.extras.entity.Test;
import com.sptek.webfw.config.springSecurity.extras.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Long> {
    Test findByMyKey(String key);

    Optional<Test> findOptByMyKey(String key); //spEL 구성의 메소드 이름을 바꾸고 싶으면 findXXXBy 형식으로 By 앞쪽에 덮붙인다.

    List<Test> findByMyKeyIn (List<String> keys);

    //For Test, Optional<List<T>> 는 사실 불필요, 테스트용
    Optional<List<Test>> findOptByMyKeyIn (List<String> keys);

}
