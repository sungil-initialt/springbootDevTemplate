package com.sptek.webfw.util;

import com.sptek.webfw.example.dto.ExampleGoodsDto;
import com.sptek.webfw.example.dto.ExampleProductDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

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
                .setSkipNullEnabled(true) //src쪽 값이 null 일때 바인딩하지 않으며 des쪽 값을 그데로 유지함
                .setAmbiguityIgnored(true); //모호한 매핑상황에서 에러를 ex를 발생시키지 않고 mapper가 판단하여 처리함

        modelMapper.createTypeMap(ExampleProductDto.class, ExampleGoodsDto.class).addMappings(
                mapper -> {
                    mapper.map(ExampleProductDto::getProductName, ExampleGoodsDto::setName);
                    mapper.map(ExampleProductDto::getProductPrice, ExampleGoodsDto::setOriginPrice);
                    mapper.map(ExampleProductDto::getQuantity, ExampleGoodsDto::setStock);
                    mapper.using((Converter<Boolean, String>) context -> context.getSource() ? "Y" : "N")
                            .map(ExampleProductDto::isAvailableReturn, ExampleGoodsDto::setAvailableSendBackYn);
        });

        /*
        btypeDtoMapper.addConverter((Converter<AtypeDto, BtypeDto>) mappingContext -> {
        AtypeDto source = mappingContext.getSource();
        BtypeDto destination = mappingContext.getDestination();
        destination.setName(source.getProductName());
        //destination.setDiscountedPrice(String.valueOf(source.getProductPrice() * (100 - source.getDiscountRate()) / 100));

        destination.setId(source.getId());
        destination.setFirstShift(source.getFirstShift() == null ? null : Time.valueOf(source.getFirstShift()));
        destination.setSecondShift(source.getSecondShift() == null ? null : Time.valueOf(source.getSecondShift()));
        destination.setEnable(true);
        destination.setAddress(source.getAddress());
        destination.setBoxCount(source.getBoxCount());
        destination.setName(source.getName());
        destination.setDateOfCreation(source.getDateOfCreation());
        */

        return modelMapper;
    }

    public static <S, D> D of(S sourceObject, Class<D> destinationType) {
        //for execute time test.
        long startNtime = System.nanoTime();

        ModelMapper modelMapper = getdefaultModelMapper();
        D result = modelMapper.map(sourceObject, destinationType);
        log.debug("Executed time : {}", (System.nanoTime()-startNtime));
        return result;
    }
}


