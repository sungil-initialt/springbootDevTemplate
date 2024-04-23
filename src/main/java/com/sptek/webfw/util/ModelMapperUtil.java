package com.sptek.webfw.util;

import com.sptek.webfw.config.springSecurity.CustomUserDetails;
import com.sptek.webfw.config.springSecurity.service.User;
import com.sptek.webfw.config.springSecurity.service.UserEntity;
import com.sptek.webfw.example.dto.AtypeDto;
import com.sptek.webfw.example.dto.BtypeDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

@Slf4j
public class ModelMapperUtil {
    public static final ModelMapper rawModelMapper = createModelMapper();

    public static ModelMapper createModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD) //MatchingStrategies.LOOSE, MatchingStrategies.STRICT
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);

        modelMapper.addConverter((Converter<AtypeDto, BtypeDto>) mappingContext -> {
            AtypeDto src = mappingContext.getSource();
            BtypeDto des = mappingContext.getDestination();
            des.setName(src.getProductName());
            //des.setDiscountedPrice(String.valueOf(src.getProductPrice()  * (100-src.getDiscountRate()) /100));
            return des;
        });

        return modelMapper;
    }

    public static ModelMapper getRawModelMapper() {
        return rawModelMapper;
    }

    public static <S, D> D getObject(S sourceObject, Class<D> destinationType) {
        //for execute time test.
        long startNtime = System.nanoTime();

        ModelMapper modelMapper = getRawModelMapper();
        D result = modelMapper.map(sourceObject, destinationType);
        log.debug("Executed time : {}", (System.nanoTime()-startNtime));
        return result;
    }

    public static ModelMapper userMapper;
    public static User getUser(UserEntity userEntity) {
        //for execute time test.
        long startNtime = System.nanoTime();

        if(userMapper == null) {
            userMapper = new ModelMapper();
        }
        User user = userMapper.map(userEntity, User.class);
        log.debug("Executed time : {}", (System.nanoTime()-startNtime));
        return user;
    }

    public static ModelMapper customUserDetailsMapper;
    public static CustomUserDetails getCustomUserDetails(UserEntity userEntity) {
        if(customUserDetailsMapper == null) {
            customUserDetailsMapper = new ModelMapper();
        }
        return customUserDetailsMapper.map(userEntity, CustomUserDetails.class);
    }


    /*
    //it's a just example for custom Converting
    public static ModelMapper btypeDtoMapper;
    public static BtypeDto getBtypeDto(AtypeDto atypeDto) {
        if(btypeDtoMapper == null) {
            btypeDtoMapper = new ModelMapper();

            btypeDtoMapper.getConfiguration().setAmbiguityIgnored(true);
            btypeDtoMapper.typeMap(AtypeDto.class, BtypeDto.class).addMappings(mapper -> {

                mapper.map(AtypeDto::getProductName, BtypeDto::setName);
                mapper.map(src -> src.getDiscountRate()
                        , (BtypeDto des, Long value)
                                -> des.setDiscountedPrice(Long.toString(Long.valueOf(des.getProductPrice()) * (100-value) /100)));
                });


//            btypeDtoMapper.typeMap(AtypeDto.class, BtypeDto.class).addMappings(mapper -> {
//                mapper.map(AtypeDto::getManufacturerName, BtypeDto::setBrand);
//                mapper.map(AtypeDto::getProductName, BtypeDto::setName);
//            });

        }
        return btypeDtoMapper.map(atypeDto, BtypeDto.class);


    }

     */
    


}


