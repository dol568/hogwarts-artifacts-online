package edu.tcu.cs.hogwartsartifactsonline.artifact;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tcu.cs.hogwartsartifactsonline.artifact.dto.ArtifactDto;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ArtifactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ArtifactService artifactService;

    @Autowired
    ObjectMapper objectMapper;

    List<Artifact> artifacts;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {
        this.artifacts = new ArrayList<>();

        Artifact artifact1 = new Artifact();
        artifact1.setId("1");
        artifact1.setName("Wand");
        artifact1.setDescription("Wand desc");
        artifact1.setImageUrl("ImageUrl1");
        artifacts.add(artifact1);

        Artifact artifact2 = new Artifact();
        artifact2.setId("2");
        artifact2.setName("Cloak");
        artifact2.setDescription("Cloak desc");
        artifact2.setImageUrl("ImageUrl2");
        artifacts.add(artifact2);

        Artifact artifact3 = new Artifact();
        artifact3.setId("3");
        artifact3.setName("Map");
        artifact3.setDescription("Map desc");
        artifact3.setImageUrl("ImageUrl3");
        artifacts.add(artifact3);

        Artifact artifact4 = new Artifact();
        artifact4.setId("4");
        artifact4.setName("Jacket");
        artifact4.setDescription("Jacket desc");
        artifact4.setImageUrl("ImageUrl4");
        artifacts.add(artifact4);

        Artifact artifact5 = new Artifact();
        artifact5.setId("5");
        artifact5.setName("Glass");
        artifact5.setDescription("Glass desc");
        artifact5.setImageUrl("ImageUrl5");
        artifacts.add(artifact5);

        Artifact artifact6 = new Artifact();
        artifact6.setId("6");
        artifact6.setName("Scar");
        artifact6.setDescription("Scar desc");
        artifact6.setImageUrl("ImageUrl6");
        artifacts.add(artifact6);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindArtifactByIdSuccess() throws Exception {

        given(this.artifactService.findById("1")).willReturn(this.artifacts.get(0));

        this.mockMvc.perform(get(this.baseUrl + "/artifacts/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.name").value("Wand"));
    }

    @Test
    void testFindArtifactByIdNotFound() throws Exception {

        given(this.artifactService.findById("1")).willThrow(new ObjectNotFoundException("artifact", "1"));

        this.mockMvc.perform(get(this.baseUrl + "/artifacts/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllArtifactsSuccess() throws Exception {

        given(this.artifactService.findAll()).willReturn(this.artifacts);

        this.mockMvc.perform(get(this.baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.artifacts.size())))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].name").value("Wand"))
                .andExpect(jsonPath("$.data[1].id").value("2"))
                .andExpect(jsonPath("$.data[1].name").value("Cloak"));
    }

    @Test
    void testAddArtifactSuccess() throws Exception {

        ArtifactDto artifactDto = new ArtifactDto(null,
                "Arifact5",
                "atrifact5desc",
                "iamgeurl",
                null);

        String json = this.objectMapper.writeValueAsString(artifactDto);

        Artifact savedArtifact = new Artifact();
        savedArtifact.setId("1220");
        savedArtifact.setName("Artifact5");
        savedArtifact.setDescription("atrifact5desc");
        savedArtifact.setImageUrl("iamgeurl");

        given(this.artifactService.save(Mockito.any(Artifact.class))).willReturn(savedArtifact);

        this.mockMvc.perform(post(this.baseUrl + "/artifacts")
                .contentType(MediaType.APPLICATION_JSON).content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(savedArtifact.getId()))
                .andExpect(jsonPath("$.data.name").value(savedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(savedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(savedArtifact.getImageUrl()));
    }

    @Test
    void testUpdateArtifactSuccess() throws Exception {

        ArtifactDto artifactDto = new ArtifactDto("1220",
                "Arifact5",
                "atrifact5desc",
                "iamgeurl",
                null);

        String json = this.objectMapper.writeValueAsString(artifactDto);

        Artifact updatedArtifact = new Artifact();
        updatedArtifact.setId("1220");
        updatedArtifact.setName("Arifact5");
        updatedArtifact.setDescription("atrifact5desc");
        updatedArtifact.setImageUrl("iamgeurl");

        given(this.artifactService.update(eq("1220"), Mockito.any(Artifact.class))).willReturn(updatedArtifact);

        this.mockMvc.perform(put(this.baseUrl + "/artifacts/1220")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value("1220"))
                .andExpect(jsonPath("$.data.name").value(updatedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(updatedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(updatedArtifact.getImageUrl()));
    }

    @Test
    void testUpdateArtifactErrorNonExistentId() throws Exception {

        ArtifactDto artifactDto = new ArtifactDto("1220",
                "Arifact5",
                "atrifact5desc",
                "iamgeurl",
                null);

        String json = this.objectMapper.writeValueAsString(artifactDto);

        given(this.artifactService.update(eq("1220"), Mockito.any(Artifact.class)))
                .willThrow(new ObjectNotFoundException("artifact", "1220"));

        this.mockMvc.perform(put(this.baseUrl + "/artifacts/1220")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1220"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteArtifactSuccesss() throws Exception {

        doNothing().when(this.artifactService).delete("1985");

        this.mockMvc.perform(delete(this.baseUrl + "/artifacts/1985").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteArtifactErrorWithNonExistentId() throws Exception {

        doThrow(new ObjectNotFoundException("artifact", "1985")).when(this.artifactService).delete("1985");

        this.mockMvc.perform(delete(this.baseUrl + "/artifacts/1985").accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message").value("Could not find artifact with Id 1985"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}