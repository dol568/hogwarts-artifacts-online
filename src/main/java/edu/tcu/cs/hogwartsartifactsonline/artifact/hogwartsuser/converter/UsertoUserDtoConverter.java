package edu.tcu.cs.hogwartsartifactsonline.artifact.hogwartsuser.converter;

import edu.tcu.cs.hogwartsartifactsonline.artifact.hogwartsuser.HogwartsUser;
import edu.tcu.cs.hogwartsartifactsonline.artifact.hogwartsuser.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UsertoUserDtoConverter implements Converter<HogwartsUser, UserDto> {

    @Override
    public UserDto convert(HogwartsUser source) {
        final UserDto userDto = new UserDto(source.getId(),source.getUsername(),source.isEnabled(),source.getRoles());
        return userDto;
    }
}
