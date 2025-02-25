package com.sptek._frameworkWebCore.config.springSecurity.extras.repository;

import com.sptek._frameworkWebCore.config.springSecurity.extras.entity.TestJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestJpaRepository extends JpaRepository<TestJpa, Long> {
    TestJpa findByMyKey(String key);

    Optional<TestJpa> findOptByMyKey(String key); //spEL 구성의 메소드 이름을 바꾸고 싶으면 findXXXBy 형식으로 By 앞쪽에 덮붙인다.

    List<TestJpa> findByMyKeyIn (List<String> keys); //List형 리턴 메소드의 경우 결과가 없어도 null 이 아니라 empty List로 넘어옴

    //For Test, Optional<List<T>> 는 사실 불필요, 테스트용
    Optional<List<TestJpa>> findOptByMyKeyIn (List<String> keys);

}
