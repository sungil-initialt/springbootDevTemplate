package com.sptek.webfw.util;

import com.sptek.webfw.config.springSecurity.service.User;
import com.sptek.webfw.config.springSecurity.service.UserEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public class ModelMapperUtil {
    public static ModelMapper rawModelMapper;
    public static ModelMapper getRawModelMapper() {
        if(rawModelMapper == null) {
            rawModelMapper = new ModelMapper();
            rawModelMapper.getConfiguration()
                    .setMatchingStrategy(MatchingStrategies.STANDARD)
                    .setSkipNullEnabled(true);
        }
        return rawModelMapper;
    }

    public static <S, D> D getObject(S sourceObject, Class<D> destinationType) {
        ModelMapper modelMapper = getRawModelMapper();
        return modelMapper.map(sourceObject, destinationType);
    }

    public static ModelMapper userFromUserEntity;
    public static User getUserFromUserEntity(UserEntity userEntity) {
        if(userFromUserEntity == null) {
            userFromUserEntity = new ModelMapper();
        }
        return userFromUserEntity.map(userEntity, User.class);
    }

}
