package edu.tcu.cs.hogwartsartifactsonline.artifact.hogwartsuser;

import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<HogwartsUser> findAll() {
        return this.userRepository.findAll();
    }

    public HogwartsUser findById(Integer hogwartsUserId) {
        return this.userRepository.findById(hogwartsUserId)
                .orElseThrow(() -> new ObjectNotFoundException("user", hogwartsUserId));
    }

    public HogwartsUser save(HogwartsUser newHogwartsUser) {
        return this.userRepository.save(newHogwartsUser);
    }

    public HogwartsUser update(Integer hogwartsUserId, HogwartsUser update) {
        HogwartsUser oldUser = this.userRepository.findById(hogwartsUserId)
                .orElseThrow(() -> new ObjectNotFoundException("user", hogwartsUserId));
        oldUser.setUsername(update.getUsername());
        oldUser.setEnabled(update.isEnabled());
        oldUser.setRoles(update.getRoles());
        return this.userRepository.save(oldUser);
    }

    public void delete(Integer hogwartsUserId) {
        this.userRepository.findById(hogwartsUserId)
                .orElseThrow(() -> new ObjectNotFoundException("user", hogwartsUserId));
        this.userRepository.deleteById(hogwartsUserId);
    }
}
