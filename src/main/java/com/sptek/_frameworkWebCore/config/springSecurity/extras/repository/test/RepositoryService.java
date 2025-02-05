package com.sptek._frameworkWebCore.config.springSecurity.extras.repository.test;

import com.sptek._projectCommon.code.ServiceErrorCodeEnum;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.config.springSecurity.extras.entity.*;
import com.sptek._frameworkWebCore.config.springSecurity.extras.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class RepositoryService {

    private final TestRepository testRepository;

    // TEST **************** 결론은 단일 반환의 경우 Optional 로 받고 복수는 List<T>로 받자 (조회값이 없더라도 empty List를 내리지 null 을 내리지 않음으로 Optional을 이중으로 할 필요가 없음)

    public Map<String, Object> testRepository(String key) {
        List<String> keys = Arrays.asList(key.split("-"));
        Map<String, Object> returnMap = new HashMap<>();

        if(keys.size() == 1){
            Test returnObj = testRepository.findByMyKey(keys.get(0));
            testRepositoryWithObj(returnObj);
            returnMap.put("returnObj", returnObj);

            Optional<Test> returnOpt = testRepository.findOptByMyKey(keys.get(0));
            testRepositoryWithOptional(returnOpt);
            returnMap.put("returnOpt", returnOpt);

        }else {
            List<Test> returnList = testRepository.findByMyKeyIn(keys);
            testRepositoryWithList(returnList);
            returnMap.put("returnList", returnList);

            Optional<List<Test>> returnListOpt = testRepository.findOptByMyKeyIn(keys);
            testRepositoryWithListOpt(returnListOpt);
            returnMap.put("returnListOpt", returnListOpt);
        }

        log.debug("strMap : {}", new HashMap<String, String>());
        return returnMap;
    }


    public void testRepositoryWithObj(Test test) {
        if(test == null) 
            log.debug("test obj is null");
        else 
            log.debug("test obj is not null : {}", test);

    }

    public void testRepositoryWithOptional(Optional<Test> testOpt) {
        //서로 반대 의미
        log.debug("isEmpty : {}" , testOpt.isEmpty());
        log.debug("isPresent : {}" , testOpt.isPresent());

        //조재하는 케이스만 처리
        testOpt.ifPresent(test -> log.debug("ifPresent : {}", test));

        //존재할때와 안할때 케이스 처리
        testOpt.ifPresentOrElse(
                test -> log.debug("ifpresendt : {}", test)
                ,() -> log.debug("ifPresentOrElse"));

        //존재하지 않을때의 대체값
        log.debug("orElse : {} ", testOpt.orElse(Test.builder().myKey("TEST NAME").build()));

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

    public void testRepositoryWithList(List<Test> tests) {
        if (tests == null){
            log.debug("tests is null"); //조회값이 없어도 List 를 내려줌 (empty 상태)
        } else {
            log.debug("tests is not null");
            
            if (tests.isEmpty()) {
                log.debug("But tests is empty"); //조회값이 없으면 empty List
            } else {
                log.debug("first test : {}", tests.get(0));
            }
        }
    }

    public void testRepositoryWithListOpt(Optional<List<Test>> testsOpt) {
        log.debug("isEmpty : {}" , testsOpt.isEmpty()); //조회값이 없더라도 항상 empty List를 가지고 있음으로 true, 내부 List는 empty 임
        log.debug("isPresent : {}" , testsOpt.isPresent());

        testsOpt.ifPresent(tests -> log.debug("ifPresent : {}", tests));

        testsOpt.ifPresentOrElse(
                tests -> log.debug("ifPresent : {}", tests)
                ,() -> log.debug("ifPresentOrElse"));
    }
}
