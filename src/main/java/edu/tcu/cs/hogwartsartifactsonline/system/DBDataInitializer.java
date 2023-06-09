package edu.tcu.cs.hogwartsartifactsonline.system;

import edu.tcu.cs.hogwartsartifactsonline.artifact.Artifact;
import edu.tcu.cs.hogwartsartifactsonline.artifact.ArtifactRepository;
import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.HogwartsUser;
import edu.tcu.cs.hogwartsartifactsonline.hogwartsuser.UserService;
import edu.tcu.cs.hogwartsartifactsonline.wizard.Wizard;
import edu.tcu.cs.hogwartsartifactsonline.wizard.WizardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DBDataInitializer implements CommandLineRunner {

    private final ArtifactRepository artifactRepository;

    private final WizardRepository wizardRepository;

    private final UserService userService;

    public DBDataInitializer(ArtifactRepository artifactRepository, WizardRepository wizardRepository, UserService userService) {
        this.artifactRepository = artifactRepository;
        this.wizardRepository = wizardRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {

        Artifact artifact1 = new Artifact();
        artifact1.setId("11");
        artifact1.setName("Wand");
        artifact1.setDescription("Wand desc");
        artifact1.setImageUrl("ImageUrl1");

        Artifact artifact2 = new Artifact();
        artifact2.setId("22");
        artifact2.setName("Cloak");
        artifact2.setDescription("Cloak desc");
        artifact2.setImageUrl("ImageUrl2");

        Artifact artifact3 = new Artifact();
        artifact3.setId("33");
        artifact3.setName("Map");
        artifact3.setDescription("Map desc");
        artifact3.setImageUrl("ImageUrl3");

        Artifact artifact4 = new Artifact();
        artifact4.setId("44");
        artifact4.setName("Jacket");
        artifact4.setDescription("Jacket desc");
        artifact4.setImageUrl("ImageUrl4");

        Artifact artifact5 = new Artifact();
        artifact5.setId("55");
        artifact5.setName("Glass");
        artifact5.setDescription("Glass desc");
        artifact5.setImageUrl("ImageUrl5");


        Artifact artifact6 = new Artifact();
        artifact6.setId("66");
        artifact6.setName("Scar");
        artifact6.setDescription("Scar desc");
        artifact6.setImageUrl("ImageUrl6");

        Wizard wizard1 = new Wizard();
        wizard1.setId(1);
        wizard1.setName("Albus");
        wizard1.addArtifact(artifact1);
        wizard1.addArtifact(artifact3);

        Wizard wizard2 = new Wizard();
        wizard2.setId(2);
        wizard2.setName("Harry");
        wizard2.addArtifact(artifact2);
        wizard2.addArtifact(artifact4);

        Wizard wizard3 = new Wizard();
        wizard3.setId(3);
        wizard3.setName("Hermiona");
        wizard3.addArtifact(artifact5);

        wizardRepository.save(wizard1);
        wizardRepository.save(wizard2);
        wizardRepository.save(wizard3);

        artifactRepository.save(artifact6);

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

        this.userService.save(user1);
        this.userService.save(user2);
        this.userService.save(user3);
    }
}
