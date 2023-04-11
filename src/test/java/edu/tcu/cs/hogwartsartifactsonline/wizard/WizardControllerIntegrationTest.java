package edu.tcu.cs.hogwartsartifactsonline.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tcu.cs.hogwartsartifactsonline.system.StatusCode;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Wizard API endpoints")
@Tag("integration")
public class WizardControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    String token;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        ResultActions resultActions = this.mockMvc
                .perform(post(this.baseUrl + "/users/login").with(httpBasic("john", "123456")));
        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        this.token = "Bearer " + json.getJSONObject("data").getString("token");
    }

    @Test
    @DisplayName("Check findAllWizards (GET)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void findAllWizardsSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/wizards").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Check findWizardById (GET)")
    void findWizardByIdSuccess() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/wizards/1").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.name").value("Albus"));
    }

    @Test
    @DisplayName("Check findWizardById with non-existent Id (GET)")
    void findWizardByIdNotFound() throws Exception {
        this.mockMvc.perform(get(this.baseUrl + "/wizards/337").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 337"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check addWizard with valid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddWizardSuccess() throws Exception {
        Wizard wizard = new Wizard();
        wizard.setName("Selena");

        String json = this.objectMapper.writeValueAsString(wizard);

        this.mockMvc.perform(post(this.baseUrl + "/wizards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("Selena"));
        this.mockMvc.perform(get(this.baseUrl + "/wizards").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(4)));
    }

    @Test
    @DisplayName("Check addWizard with invalid input (POST)")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testAddWizardErrorWithInvalidInput() throws Exception {
        Wizard wizard = new Wizard();
        wizard.setName("");

        String json = this.objectMapper.writeValueAsString(wizard);

        this.mockMvc.perform(post(this.baseUrl + "/wizards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details"))
                .andExpect(jsonPath("$.data.name").value("name is required"));
        this.mockMvc.perform(get(this.baseUrl + "/wizards").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Check updateWizard with valid input (PUT)")
    void testUpdateWizardSuccess() throws Exception {
        Wizard wizard = new Wizard();
        wizard.setId(1);
        wizard.setName("Wizard update");

        String json = this.objectMapper.writeValueAsString(wizard);

        this.mockMvc.perform(put(this.baseUrl + "/wizards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Wizard update"));
    }

    @Test
    @DisplayName("Check updateWizard with non-existent Id (PUT)")
    void testUpdateWizardErrorWithNonExistentId() throws Exception {
        Wizard wizard = new Wizard();
        wizard.setId(115);
        wizard.setName("Wizard update");

        String json = this.objectMapper.writeValueAsString(wizard);

        this.mockMvc.perform(put(this.baseUrl + "/wizards/115")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 115"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check updateWizard with invalid input (PUT)")
    void testUpdateWizardErrorWithInvalidInput() throws Exception {
        Wizard wizard = new Wizard();
        wizard.setId(1);
        wizard.setName("");

        String json = this.objectMapper.writeValueAsString(wizard);

        this.mockMvc.perform(put(this.baseUrl + "/wizards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", this.token)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, see data for details"))
                .andExpect(jsonPath("$.data.name").value("name is required"));
        this.mockMvc.perform(get(this.baseUrl + "/wizards/1").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Albus"));
    }

    @Test
    @DisplayName("Check deleteWizard with valid input (DELETE)")
    void deleteWizardSuccess() throws Exception {
        this.mockMvc.perform(delete(this.baseUrl + "/wizards/3").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"));
        this.mockMvc.perform(get(this.baseUrl + "/wizards/3").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 3"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check deleteWizard with non-existent Id (DELETE)")
    void deleteWizardErrorWithNonExistentId() throws Exception {
        this.mockMvc.perform(delete(this.baseUrl + "/wizards/337").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 337"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check assignArtifact with valid ids (PUT)")
    void testAssignArtifactToWizardSuccess() throws Exception {
        this.mockMvc.perform(put(this.baseUrl + "/wizards/1/artifacts/66").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Artifact Assignment Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check assignArtifact with non-existent Wizard Id (PUT)")
    void testAssignArtifactToWizardErrorWithNonExistentWizardId() throws Exception {
        this.mockMvc.perform(put(this.baseUrl + "/wizards/110/artifacts/66").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 110"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Check assignArtifact with non-existent Artifact Id (PUT)")
    void testAssignArtifactToWizardErrorWithNonExistentArtifactId() throws Exception {
        this.mockMvc.perform(put(this.baseUrl + "/wizards/1/artifacts/666").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 666"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}