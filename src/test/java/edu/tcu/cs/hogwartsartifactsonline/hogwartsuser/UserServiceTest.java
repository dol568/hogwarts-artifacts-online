package edu.tcu.cs.hogwartsartifactsonline.hogwartsuser;

import edu.tcu.cs.hogwartsartifactsonline.artifact.hogwartsuser.HogwartsUser;
import edu.tcu.cs.hogwartsartifactsonline.artifact.hogwartsuser.UserRepository;
import edu.tcu.cs.hogwartsartifactsonline.artifact.hogwartsuser.UserService;
import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.h2.engine.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    List<HogwartsUser> users;

    @BeforeEach
    void setUp() {
        HogwartsUser user1 = new HogwartsUser();
        user1.setId(1);
        user1.setUsername("john");
        user1.setPassword("123456");
        user1.setEnabled(true);
        user1.setRoles("admin user");

        HogwartsUser user2 = new HogwartsUser();
        user2.setId(2);
        user2.setUsername("eric");
        user2.setPassword("654321");
        user2.setEnabled(true);
        user2.setRoles("user");

        HogwartsUser user3 = new HogwartsUser();
        user3.setId(3);
        user3.setUsername("tom");
        user3.setEnabled(false);
        user3.setPassword("qwerty");
        user3.setRoles("user");

        this.users = new ArrayList<>();
        this.users.add(user1);
        this.users.add(user2);
        this.users.add(user3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAllUsersSuccess() {
        given(this.userRepository.findAll()).willReturn(this.users);

        List<HogwartsUser> actualUsers = this.userService.findAll();

        assertThat(actualUsers.size()).isEqualTo(this.users.size());
        verify(this.userRepository, times(1)).findAll();
    }

    @Test
    void findUserByIdSuccess() {
        given(this.userRepository.findById(1)).willReturn(Optional.of(this.users.get(0)));

        HogwartsUser returnedUser = this.userService.findById(1);
        assertThat(returnedUser.getId()).isEqualTo(this.users.get(0).getId());
        assertThat(returnedUser.getUsername()).isEqualTo(this.users.get(0).getUsername());
        assertThat(returnedUser.getPassword()).isEqualTo(this.users.get(0).getPassword());
        assertThat(returnedUser.getRoles()).isEqualTo(this.users.get(0).getRoles());
        assertThat(returnedUser.isEnabled()).isEqualTo(this.users.get(0).isEnabled());
        verify(this.userRepository, times(1)).findById(1);
    }

    @Test
    void findUserByIdNotFound() {
        given(this.userRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> {
            HogwartsUser returnedUSer = this.userService.findById(1);
        });

        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                        .hasMessage("Could not find user with Id 1");

        verify(this.userRepository, times(1)).findById(Mockito.any(Integer.class));
    }

    @Test
    void saveUserSuccess() {
        HogwartsUser newUser = new HogwartsUser();
        newUser.setUsername("phil");
        newUser.setPassword("1234*#");
        newUser.setEnabled(true);
        newUser.setRoles("admin user");

        given(this.userRepository.save(newUser)).willReturn(newUser);

        HogwartsUser returnedUser = this.userService.save(newUser);

        assertThat(returnedUser.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(returnedUser.isEnabled()).isEqualTo(newUser.isEnabled());
        assertThat(returnedUser.getPassword()).isEqualTo(newUser.getPassword());
        assertThat(returnedUser.getRoles()).isEqualTo(newUser.getRoles());
        verify(this.userRepository, times(1)).save(newUser);
    }

    @Test
    void updateUserSuccess() {
        HogwartsUser update = new HogwartsUser();
        update.setUsername("phil");
        update.setPassword("1234*#");
        update.setEnabled(true);
        update.setRoles("admin user");

        given(this.userRepository.findById(1)).willReturn(Optional.of(this.users.get(0)));
        given((this.userRepository.save(this.users.get(0)))).willReturn(this.users.get(0));

        HogwartsUser updatedUser = this.userService.update(1, update);

        assertThat(updatedUser.getId()).isEqualTo(1);
        assertThat(updatedUser.getUsername()).isEqualTo(update.getUsername());
        verify(this.userRepository, times(1)).findById(1);
        verify(this.userRepository, times(1)).save(this.users.get(0));
    }

    @Test
    void updateUserNotFound() {
        HogwartsUser update = new HogwartsUser();
        update.setUsername("phil");
        update.setPassword("1234*#");
        update.setEnabled(true);
        update.setRoles("admin user");

        given(this.userRepository.findById(1)).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            this.userService.update(1, update);
        });

        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find user with Id 1");
        verify(this.userRepository, times(1)).findById(1);
    }

    @Test
    void deleteUserSuccess() {
        given(this.userRepository.findById(1)).willReturn(Optional.of(this.users.get(0)));
        doNothing().when(this.userRepository).deleteById(1);

        this.userService.delete(1);

        verify(this.userRepository, times(1)).deleteById(1);
        verify(this.userRepository, times(1)).findById(1);
    }

    @Test
    void deleteUserNotFound() {
        given(this.userRepository.findById(1)).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            this.userService.delete(1);
        });

        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find user with Id 1");
        verify(this.userRepository, times(1)).findById(1);
    }
}