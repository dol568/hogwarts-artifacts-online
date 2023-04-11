package edu.tcu.cs.hogwartsartifactsonline.hogwartsuser;

import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.converter.UserDtoToUserConverter;
import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.converter.UsertoUserDtoConverter;
import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.dto.UserDto;
import edu.tcu.cs.hogwartsartifactsonline.system.Result;
import edu.tcu.cs.hogwartsartifactsonline.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    private final UserService userService;

    private final UserDtoToUserConverter userDtoToUserConverter;

    private final UsertoUserDtoConverter usertoUserDtoConverter;

    public UserController(UserService userService, UserDtoToUserConverter userDtoToUserConverter, UsertoUserDtoConverter usertoUserDtoConverter) {
        this.userService = userService;
        this.userDtoToUserConverter = userDtoToUserConverter;
        this.usertoUserDtoConverter = usertoUserDtoConverter;
    }

    @GetMapping
    public Result findAllUsers() {
        List<HogwartsUser> hogwartsUsers = this.userService.findAll();
        List<UserDto> hogwartsUsersDtos = hogwartsUsers.stream()
                .map(this.usertoUserDtoConverter::convert)
                .collect(Collectors.toList());
        return new Result(true, StatusCode.SUCCESS, "Find All Success", hogwartsUsersDtos);
    }

    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable Integer userId) {
        HogwartsUser hogwartsUser = this.userService.findById(userId);
        UserDto userDto = this.usertoUserDtoConverter.convert(hogwartsUser);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", userDto);
    }

    @PostMapping
    public Result addUser(@Valid @RequestBody HogwartsUser hogwartsUser) {
        HogwartsUser savedUser = this.userService.save(hogwartsUser);
        UserDto savedUserDto = this.usertoUserDtoConverter.convert(savedUser);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedUserDto);
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable Integer userId, @Valid @RequestBody UserDto userDto) {
        HogwartsUser hogwartsUser = this.userDtoToUserConverter.convert(userDto);
        HogwartsUser updatedUser = this.userService.update(userId, hogwartsUser);
        UserDto updatedUserDto = this.usertoUserDtoConverter.convert(updatedUser);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable Integer userId) {
        this.userService.delete(userId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }

}
