package com.sptek.webfw.util;

import com.sptek.webfw.example.dto.ExampleADto;
import com.sptek.webfw.example.dto.ExampleBDto;
import com.sptek.webfw.example.dto.ExampleGoodsDto;
import com.sptek.webfw.example.dto.ExampleProductDto;
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
    public static final ModelMapper defaultModelMapper = createDefaultModelMapper();

    public static ModelMapper getdefaultModelMapper() {
        return defaultModelMapper;
    }

    public static ModelMapper createDefaultModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD) //MatchingStrategies.LOOSE, MatchingStrategies.STRICT
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) //get,set이 없는 외부 클레의 private 필드에 직접 접근가능
                .setSkipNullEnabled(true) //src쪽 값이 null 일때 바인딩하지 않으며 des쪽 값을 그데로 유지함
                .setAmbiguityIgnored(true); //모호한 매핑상황에서 에러를 ex를 발생시키지 않고 mapper가 판단하여 처리함

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

    //실행시간 테스트를 위해 임시로 만듬
    public static <S, D> D map(S sourceObject, Class<D> destinationType) {
        //for execute time test.
        long starttime = System.currentTimeMillis();

        ModelMapper modelMapper = getdefaultModelMapper();
        D result = modelMapper.map(sourceObject, destinationType);
        log.debug("Executed time : {}", (System.currentTimeMillis()-starttime));
        return result;
    }

}


