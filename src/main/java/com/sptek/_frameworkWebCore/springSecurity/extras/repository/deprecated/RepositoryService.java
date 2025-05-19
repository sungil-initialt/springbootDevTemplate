package com.sptek._frameworkWebCore.springSecurity.extras.repository.deprecated;

import com.sptek._frameworkWebCore.springSecurity.extras.entity.TestJpa;
import com.sptek._frameworkWebCore.springSecurity.extras.repository.TestJpaRepository;
import com.sptek._projectCommon.code.ServiceErrorCodeEnum;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class RepositoryService {

    private final TestJpaRepository testJpaRepository;

    // TEST **************** 결론은 단일 반환의 경우 Optional 로 받고 복수는 List<T>로 받자 (조회값이 없더라도 empty List를 내리지 null 을 내리지 않음으로 Optional을 이중으로 할 필요가 없음)

    public Map<String, Object> testRepository(String key) {
        List<String> keys = Arrays.asList(key.split("-"));
        Map<String, Object> returnMap = new HashMap<>();

        if(keys.size() == 1){
            TestJpa returnObj = testJpaRepository.findByMyKey(keys.get(0));
            testRepositoryWithObj(returnObj);
            returnMap.put("returnObj", returnObj);

            Optional<TestJpa> returnOpt = testJpaRepository.findOptByMyKey(keys.get(0));
            testRepositoryWithOptional(returnOpt);
            returnMap.put("returnOpt", returnOpt);

        }else {
            List<TestJpa> returnList = testJpaRepository.findByMyKeyIn(keys);
            testRepositoryWithList(returnList);
            returnMap.put("returnList", returnList);

            Optional<List<TestJpa>> returnListOpt = testJpaRepository.findOptByMyKeyIn(keys);
            testRepositoryWithListOpt(returnListOpt);
            returnMap.put("returnListOpt", returnListOpt);
        }

        log.debug("strMap : {}", new HashMap<String, String>());
        return returnMap;
    }


    public void testRepositoryWithObj(TestJpa testJpa) {
        if(testJpa == null)
            log.debug("test obj is null");
        else 
            log.debug("test obj is not null : {}", testJpa);

    }

    public void testRepositoryWithOptional(Optional<TestJpa> testOpt) {
        //서로 반대 의미
        log.debug("isEmpty : {}" , testOpt.isEmpty());
        log.debug("isPresent : {}" , testOpt.isPresent());

        //조재하는 케이스만 처리
        testOpt.ifPresent(testJpa -> log.debug("ifPresent : {}", testJpa));

        //존재할때와 안할때 케이스 처리
        testOpt.ifPresentOrElse(
                testJpa -> log.debug("ifpresendt : {}", testJpa)
                ,() -> log.debug("ifPresentOrElse"));

        //존재하지 않을때의 대체값
        log.debug("orElse : {} ", testOpt.orElse(TestJpa.builder().myKey("TEST NAME").build()));

        //존재하지 않을때의 Exception 처리
        try {
            testOpt.orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR));
        } catch (ServiceException ex) {
            log.debug(ex.getMessage());
        }

        try {
            log.debug("testOpt.get() : {}", testOpt.get()); //empty 상태 일때는 NoSuchElementException 발생
        } catch (NoSuchElementException ex) {
            log.debug(ex.getMessage());
        }
    }

    public void testRepositoryWithList(List<TestJpa> testJpas) {
        if (testJpas == null){
            log.debug("tests is null"); //조회값이 없어도 List 를 내려줌 (empty 상태)
        } else {
            log.debug("tests is not null");
            
            if (testJpas.isEmpty()) {
                log.debug("But tests is empty"); //조회값이 없으면 empty List
            } else {
                log.debug("first test : {}", testJpas.get(0));
            }
        }
    }

    public void testRepositoryWithListOpt(Optional<List<TestJpa>> testsOpt) {
        log.debug("isEmpty : {}" , testsOpt.isEmpty()); //조회값이 없더라도 항상 empty List를 가지고 있음으로 true, 내부 List는 empty 임
        log.debug("isPresent : {}" , testsOpt.isPresent());

        testsOpt.ifPresent(tests -> log.debug("ifPresent : {}", tests));

        testsOpt.ifPresentOrElse(
                tests -> log.debug("ifPresent : {}", tests)
                ,() -> log.debug("ifPresentOrElse"));
    }
}
