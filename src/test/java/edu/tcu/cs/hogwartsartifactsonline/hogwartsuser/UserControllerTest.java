package edu.tcu.cs.hogwartsartifactsonline.hogwartsuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.HogwartsUser;
import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.dto.UserDto;
import edu.tcu.cs.hogwartsartifactsonline.system.StatusCode;
import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

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
    void findAllUsersSuccess() throws Exception {
        given(this.userService.findAll()).willReturn(this.users);

        this.mockMvc.perform(get(baseUrl + "/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.users.size())))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].username").value("john"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].username").value("eric"));
    }

    @Test
    void findUserByIdSuccess() throws Exception {
        given(this.userService.findById(2)).willReturn(this.users.get(1));

        this.mockMvc.perform(get(baseUrl + "/users/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.username").value("eric"));
    }

    @Test
    void findUserByIdNotFound() throws Exception {
        given(this.userService.findById(5)).willThrow(new ObjectNotFoundException("user", 5));

        this.mockMvc.perform(get(baseUrl + "/users/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 5"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void addUserSuccess() throws Exception {
        HogwartsUser user = new HogwartsUser();
        user.setId(6);
        user.setUsername("emil");
        user.setPassword("123456");
        user.setEnabled(true);
        user.setRoles("admin user");

        String json = this.objectMapper.writeValueAsString(user);

        given(this.userService.save(Mockito.any(HogwartsUser.class))).willReturn(user);

        this.mockMvc.perform(post(this.baseUrl + "/users").contentType(MediaType.APPLICATION_JSON).content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value("emil"))
                .andExpect(jsonPath("$.data.enabled").value("true"))
                .andExpect(jsonPath("$.data.roles").value("admin user"));
    }

    @Test
    void updateUserSuccess() throws Exception {
        UserDto userDto = new UserDto(3,"tommy", false, "user");

        HogwartsUser userUpdate = new HogwartsUser();
        userUpdate.setId(3);
        userUpdate.setUsername("tommy");
        userUpdate.setEnabled(false);
        userUpdate.setRoles("user");

        String json = this.objectMapper.writeValueAsString(userDto);

        given(this.userService.update(eq(3), Mockito.any(HogwartsUser.class))).willReturn(userUpdate);

        this.mockMvc.perform(put(this.baseUrl + "/users/3").contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(3))
                .andExpect(jsonPath("$.data.username").value("tommy"))
                .andExpect(jsonPath("$.data.enabled").value("false"))
                .andExpect(jsonPath("$.data.roles").value("user"));
    }

    @Test
    void updateUserNotFound() throws Exception {
        UserDto userDto = new UserDto(5,"tommy", false, "user");

        String json = this.objectMapper.writeValueAsString(userDto);

        given(this.userService.update(eq(5), Mockito.any(HogwartsUser.class))).willThrow(new ObjectNotFoundException("user", 5));

        this.mockMvc.perform(put(baseUrl + "/users/5").contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 5"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteUserSuccess() throws Exception {
        doNothing().when(this.userService).delete(2);

        this.mockMvc.perform(delete(this.baseUrl + "/users/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"));
    }

    @Test
    void deleteUserNotFound() throws Exception {
        doThrow(new ObjectNotFoundException("user", 5)).when(this.userService).delete(5);

        this.mockMvc.perform(delete(this.baseUrl + "/users/5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find user with Id 5"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}