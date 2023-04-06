package edu.tcu.cs.hogwartsartifactsonline.artifact;

import edu.tcu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import edu.tcu.cs.hogwartsartifactsonline.wizard.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifacts;

    @BeforeEach
    void setUp() {
        Artifact artifact1 = new Artifact();
        artifact1.setId("1");
        artifact1.setName("Wand");
        artifact1.setDescription("Wand desc");
        artifact1.setImageUrl("ImageUrl1");

        Artifact artifact2 = new Artifact();
        artifact2.setId("2");
        artifact2.setName("Cloak");
        artifact2.setDescription("Cloak desc");
        artifact2.setImageUrl("ImageUrl2");

        this.artifacts = new ArrayList<>();
        this.artifacts.add(artifact1);
        this.artifacts.add(artifact2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        Artifact artifact = new Artifact();
        artifact.setId("1250");
        artifact.setName("Cloak");
        artifact.setDescription("Cloak description");
        artifact.setImageUrl("imageUrl");

        Wizard wizard = new Wizard();
        wizard.setId(2);
        wizard.setName("Harry Potter");

        artifact.setOwner(wizard);

        given(artifactRepository.findById("1250")).willReturn(Optional.of(artifact));

        Artifact returnedArtifact = artifactService.findById("1250");

        assertThat(returnedArtifact.getId()).isEqualTo(artifact.getId());
        assertThat(returnedArtifact.getName()).isEqualTo(artifact.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(artifact.getDescription());
        assertThat(returnedArtifact.getImageUrl()).isEqualTo(artifact.getImageUrl());
        verify(artifactRepository, times(1)).findById("1250");
    }

    @Test
    void testFindByIdNotFound() {
        given(artifactRepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> {
            Artifact returnedArtifact = artifactService.findById("1250");
        });

        assertThat(thrown)
                .isInstanceOf(ArtifactNotFoundException.class)
                .hasMessage("Could not find artifact with Id 1250");
        verify(artifactRepository, times(1)).findById("1250");
    }

    @Test
    void testFindAllSuccess() {
        given(artifactRepository.findAll()).willReturn(this.artifacts);

        List<Artifact> actualArtifacts = artifactService.findAll();

        assertThat(actualArtifacts.size()).isEqualTo(this.artifacts.size());
        verify(artifactRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        Artifact newArtifact = new Artifact();
        newArtifact.setName("Artifact3");
        newArtifact.setDescription("Artifact3desc...");
        newArtifact.setImageUrl("ImageUrl...");

        given(idWorker.nextId()).willReturn(123456L);
        given(artifactRepository.save(newArtifact)).willReturn(newArtifact);

        Artifact savedArtifact = artifactService.save(newArtifact);

        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());
        verify(artifactRepository, times(1)).save(newArtifact);
    }

    @Test
    void testUpdateSuccess() {
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("1250");
        oldArtifact.setName("Cloak");
        oldArtifact.setDescription("Cloak description");
        oldArtifact.setImageUrl("imageUrl");

        Artifact update = new Artifact();
        update.setId("1250");
        update.setName("Cloak");
        update.setDescription("Update");
        update.setImageUrl("imageUrl");

        given(artifactRepository.findById("1250")).willReturn(Optional.of(oldArtifact));
        given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);

        Artifact updatedArtifact = this.artifactService.update("1250", update);

        assertThat(updatedArtifact.getId()).isEqualTo(update.getId());
        assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());
        verify(artifactRepository, times(1)).findById("1250");
        verify(artifactRepository, times(1)).save(oldArtifact);
    }

    @Test
    void testUpdateNotFound() {
        Artifact update = new Artifact();
        update.setName("Cloak");
        update.setDescription("Update");
        update.setImageUrl("imageUrl");

        given(artifactRepository.findById("1250")).willReturn(Optional.empty());

        assertThrows(ArtifactNotFoundException.class, () -> {
            this.artifactService.update("1250", update);
        });
        verify(artifactRepository, times(1)).findById("1250");
    }

    @Test
    void testDeleteSuccess() {
        Artifact artifact = new Artifact();
        artifact.setId("1985");
        artifact.setName("Cloak");
        artifact.setDescription("Update");
        artifact.setImageUrl("imageUrl");

        given(artifactRepository.findById("1985")).willReturn(Optional.of(artifact));
        doNothing().when(artifactRepository).deleteById("1985");

        artifactService.delete("1985");
        verify(artifactRepository, times(1)).deleteById("1985");
    }

    @Test
    void testDeleteNotFound() {

        given(artifactRepository.findById("1985")).willReturn(Optional.empty());

        assertThrows(ArtifactNotFoundException.class, () -> this.artifactService.delete("1985"));

        verify(artifactRepository, times(1)).findById("1985");
    }
}