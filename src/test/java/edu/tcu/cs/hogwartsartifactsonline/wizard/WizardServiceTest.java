package edu.tcu.cs.hogwartsartifactsonline.wizard;

import edu.tcu.cs.hogwartsartifactsonline.artifact.Artifact;
import edu.tcu.cs.hogwartsartifactsonline.artifact.ArtifactRepository;
import edu.tcu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WizardServiceTest {

    @Mock
    WizardRepository wizardRepository;

    @Mock
    ArtifactRepository artifactRepository;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizards;

    @BeforeEach
    void setUp() {
        Wizard wizard1 = new Wizard();
        wizard1.setId(1985);
        wizard1.setName("Harry");

        Wizard wizard2 = new Wizard();
        wizard2.setId(1986);
        wizard2.setName("Hermiona");

        this.wizards = new ArrayList<>();
        this.wizards.add(wizard1);
        this.wizards.add(wizard2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindById() {
        Wizard wizard = new Wizard();
        wizard.setId(1100);
        wizard.setName("Selena");

        given(this.wizardRepository.findById(1100)).willReturn(Optional.of(wizard));

        Wizard returnedWizard = this.wizardService.findById(1100);

        assertThat(returnedWizard.getId()).isEqualTo(wizard.getId());
        assertThat(returnedWizard.getName()).isEqualTo(wizard.getName());
        verify(this.wizardRepository, times(1)).findById(1100);
    }

    @Test
    void testFindByIdNotFound() {
        given(this.wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> {
            Wizard returnedWizard = this.wizardService.findById(1100);
        });

        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id 1100");
        verify(this.wizardRepository, times(1)).findById(Mockito.any(Integer.class));
    }

    @Test
    void testFindAllWizardsSuccess() {
        given(this.wizardRepository.findAll()).willReturn(this.wizards);

        List<Wizard> actualWizards = this.wizardService.findAll();

        assertThat(actualWizards.size()).isEqualTo(this.wizards.size());
        verify(this.wizardRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess() {
        Wizard newWizard = new Wizard();
        newWizard.setName("Tamisa");

        given(this.wizardRepository.save(newWizard)).willReturn(newWizard);

        Wizard savedWizard = this.wizardService.save(newWizard);

        assertThat(savedWizard.getName()).isEqualTo(newWizard.getName());
        verify(this.wizardRepository, times(1)).save(newWizard);
    }

    @Test
    void testUpdateSuccess() {
        Wizard oldWizard = new Wizard();
        oldWizard.setId(100);
        oldWizard.setName("Tamisa");

        Wizard update = new Wizard();
        update.setName("Alana");

        given(this.wizardRepository.findById(100)).willReturn(Optional.of(oldWizard));
        given(this.wizardRepository.save(oldWizard)).willReturn(oldWizard);

        Wizard updatedWizard = this.wizardService.update(100, update);

        assertThat(updatedWizard.getId()).isEqualTo(100);
        assertThat(updatedWizard.getName()).isEqualTo(update.getName());
        verify(this.wizardRepository, times(1)).findById(100);
        verify(this.wizardRepository, times(1)).save(oldWizard);
    }

    @Test
    void testUpdateNotFound() {
        Wizard update = new Wizard();
        update.setName("Alana");

        given(this.wizardRepository.findById(100)).willReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            this.wizardService.update(100, update);
        });
        verify(this.wizardRepository, times(1)).findById(100);
    }

    @Test
    void testDeleteSuccess() {
        Wizard wizard = new Wizard();
        wizard.setId(100);
        wizard.setName("Alana");

        given(this.wizardRepository.findById(100)).willReturn(Optional.of(wizard));
        doNothing().when(this.wizardRepository).deleteById(100);

        this.wizardService.delete(100);
        verify(this.wizardRepository, times(1)).deleteById(100);
    }

    @Test
    void testDeleteNotFound() {
        given(this.wizardRepository.findById(100)).willReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> this.wizardService.delete(100));

        verify(this.wizardRepository, times(1)).findById(100);
    }

    @Test
    void testAssignArtifactSuccess() {
        Artifact artifact = new Artifact();
        artifact.setId("1985");
        artifact.setName("Cloak");
        artifact.setDescription("Update");
        artifact.setImageUrl("imageUrl");

        Wizard wizard2 = new Wizard();
        wizard2.setId(2);
        wizard2.setName("Alana");
        wizard2.addArtifact(artifact);

        Wizard wizard3 = new Wizard();
        wizard3.setId(3);
        wizard3.setName("Alana");

        given(this.artifactRepository.findById("1985")).willReturn(Optional.of(artifact));
        given(this.wizardRepository.findById(3)).willReturn(Optional.of(wizard3));

        this.wizardService.assignArtifact(3, "1985");

        assertThat(artifact.getOwner().getId()).isEqualTo(3);
        assertThat(wizard3.getArtifacts()).contains(artifact);
    }

    @Test
    void testAssignArtifactErrorWithNonExistentWizardId() {
        Artifact artifact = new Artifact();
        artifact.setId("1985");
        artifact.setName("Cloak");
        artifact.setDescription("Update");
        artifact.setImageUrl("imageUrl");

        Wizard wizard2 = new Wizard();
        wizard2.setId(2);
        wizard2.setName("Alana");
        wizard2.addArtifact(artifact);

        given(this.artifactRepository.findById("1985")).willReturn(Optional.of(artifact));
        given(this.wizardRepository.findById(3)).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            this.wizardService.assignArtifact(3, "1985");
        });

        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id 3");
        assertThat(artifact.getOwner().getId()).isEqualTo(2);
    }

    @Test
    void testAssignArtifactErrorWithNonExistentArtifactId() {

        given(this.artifactRepository.findById("1985")).willReturn(Optional.empty());

        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            this.wizardService.assignArtifact(3, "1985");
        });

        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id 1985");
    }
}