package com.sptek._frameworkWebCore.config.modelMapper;

import com.sptek._frameworkWebCore.example.dto.ExampleADto;
import com.sptek._frameworkWebCore.example.dto.ExampleBDto;
import com.sptek._frameworkWebCore.example.dto.ExampleGoodsDto;
import com.sptek._frameworkWebCore.example.dto.ExampleProductDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
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
}