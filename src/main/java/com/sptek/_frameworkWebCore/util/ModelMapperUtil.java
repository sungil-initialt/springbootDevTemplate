package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore._example.dto.ExampleADto;
import com.sptek._frameworkWebCore._example.dto.ExampleBDto;
import com.sptek._frameworkWebCore._example.dto.ExampleGoodsDto;
import com.sptek._frameworkWebCore._example.dto.ExampleProductDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

/* 
ModelMapperConfig.java 를 통해 Bean 형태의 컨테이너 관리방식으로 설정되어 있음(둘중 사용성이 뭐가 좋을까??)
Mapper의 TypeMap을 cache 한 상태로 유지하기 위해 singleton(static) 방식으로만 사용할것
*/
@Slf4j
public class ModelMapperUtil {
    private static final ModelMapper defaultModelMapper = createDefaultModelMapper();

    private static ModelMapper getdefaultModelMapper() {
        return defaultModelMapper;
    }

    private static ModelMapper createDefaultModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD) //MatchingStrategies.LOOSE, MatchingStrategies.STRICT
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) //get,set이 없는 외부 클레의 private 필드에 직접 접근 가능
                .setSkipNullEnabled(true) //src 쪽 값이 null 일때 바인딩 하지 않으며 des쪽 값을 그데로 유지함
                .setAmbiguityIgnored(true); //모호한 매핑 상황 에서 에러를 ex를 발생 시키지 않고 mapper 가 판단 하여 처리함

        //todo: 계속해서 추가? (괜찮은 방법일까? 고민필요)
        modelMapper.createTypeMap(ExampleProductDto.class, ExampleGoodsDto.class).addMappings(
                mapper -> {
                    mapper.map(ExampleProductDto::getProductName, ExampleGoodsDto::setName);
                    mapper.map(ExampleProductDto::getProductPrice, ExampleGoodsDto::setOriginPrice);
                    mapper.map(ExampleProductDto::getQuantity, ExampleGoodsDto::setStock);
                    mapper.using((Converter<Boolean, String>) context -> context.getSource() ? "Y" : "N")
                            .map(ExampleProductDto::isAvailableReturn, ExampleGoodsDto::setAvailableSendBackYn);
        });

        modelMapper.createTypeMap(ExampleADto.class, ExampleBDto.class).addMappings(
                mapper -> {
                    mapper.map(ExampleADto::getADtoLastName, ExampleBDto::setBObjectEndTitle);
                    mapper.map(ExampleADto::getADtoFirstName, ExampleBDto::setBObjectFamilyTitle);
                });

        return modelMapper;
    }

    //실행 시간 테스트 를 위해 임시로 만듬
    public static <S, D> D map(S sourceObject, Class<D> destinationType) {
        //for execute time test.
        long starttime = System.currentTimeMillis();

        ModelMapper modelMapper = getdefaultModelMapper();
        D result = modelMapper.map(sourceObject, destinationType);
        log.debug("Executed time : {}", (System.currentTimeMillis()-starttime));
        return result;
    }

}


