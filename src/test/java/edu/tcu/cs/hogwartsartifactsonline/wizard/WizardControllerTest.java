package edu.tcu.cs.hogwartsartifactsonline.wizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tcu.cs.hogwartsartifactsonline.system.StatusCode;
import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import edu.tcu.cs.hogwartsartifactsonline.wizard.dto.WizardDto;
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
@AutoConfigureMockMvc
class WizardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WizardService wizardService;

    List<Wizard> wizards;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {
        this.wizards = new ArrayList<>();

        Wizard wizard1 = new Wizard();
        wizard1.setId(1000);
        wizard1.setName("Harry");

        Wizard wizard2 = new Wizard();
        wizard2.setId(1100);
        wizard2.setName("Hermiona");

        Wizard wizard3 = new Wizard();
        wizard3.setId(1110);
        wizard3.setName("Selena");

        this.wizards.add(wizard1);
        this.wizards.add(wizard2);
        this.wizards.add(wizard3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindWizardByIdSuccess() throws Exception {
        given(this.wizardService.findById(1000)).willReturn(this.wizards.get(0));

        this.mockMvc.perform(get(this.baseUrl + "/wizards/1000").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(1000))
                .andExpect(jsonPath("$.data.name").value("Harry"));
    }

    @Test
    void testFindWizardByIdNotFound() throws Exception {
        given(this.wizardService.findById(1000)).willThrow(new ObjectNotFoundException("wizard", 1000));

        this.mockMvc.perform(get(this.baseUrl + "/wizards/1000").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 1000"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllWizardsSuccess() throws Exception {
        given(this.wizardService.findAll()).willReturn(this.wizards);

        this.mockMvc.perform(get(this.baseUrl + "/wizards").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.wizards.size())))
                .andExpect(jsonPath("$.data[0].id").value(1000))
                .andExpect(jsonPath("$.data[0].name").value("Harry"))
                .andExpect(jsonPath("$.data[1].id").value(1100))
                .andExpect(jsonPath("$.data[1].name").value("Hermiona"));
    }

    @Test
    void testAddWizardSuccess() throws Exception {
        WizardDto wizardDto = new WizardDto(null, "Hugh", 0);

        String json = this.objectMapper.writeValueAsString(wizardDto);

        Wizard savedWizard = new Wizard();
        savedWizard.setId(1);
        savedWizard.setName("Hugh");

        given(this.wizardService.save(Mockito.any(Wizard.class))).willReturn(savedWizard);

        this.mockMvc.perform(post(this.baseUrl + "/wizards")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("Hugh"));
    }

    @Test
    void testUpdateWizardSuccess() throws Exception {
        WizardDto wizardDto = new WizardDto(null, "Reno", 0);

        String json = this.objectMapper.writeValueAsString(wizardDto);

        Wizard updatedWizard = new Wizard();
        updatedWizard.setId(1);
        updatedWizard.setName("Reno");

        given(this.wizardService.update(eq(1), Mockito.any(Wizard.class))).willReturn(updatedWizard);

        this.mockMvc.perform(put(this.baseUrl + "/wizards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value(updatedWizard.getName()));
    }

    @Test
    void testUpdateWizardNotFound() throws Exception {
        WizardDto wizardDto = new WizardDto(null, "Reno", 0);

        String json = this.objectMapper.writeValueAsString(wizardDto);

        given(this.wizardService.update(eq(1), Mockito.any(Wizard.class)))
                .willThrow(new ObjectNotFoundException("wizard", 1));

        this.mockMvc.perform(put(this.baseUrl + "/wizards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteWizardSuccess() throws Exception {
        doNothing().when(this.wizardService).delete(1);

        this.mockMvc.perform(delete(this.baseUrl + "/wizards/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteWizardNotFound() throws Exception {
        doThrow(new ObjectNotFoundException("wizard", 1)).when(this.wizardService).delete(1);

        this.mockMvc.perform(delete(this.baseUrl + "/wizards/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find wizard with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}