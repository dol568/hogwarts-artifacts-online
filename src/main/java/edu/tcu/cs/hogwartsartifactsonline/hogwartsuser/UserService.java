package edu.tcu.cs.hogwartsartifactsonline.hogwartsuser;

import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<HogwartsUser> findAll() {
        return this.userRepository.findAll();
    }

    public HogwartsUser findById(Integer hogwartsUserId) {
        return this.userRepository.findById(hogwartsUserId)
                .orElseThrow(() -> new ObjectNotFoundException("user", hogwartsUserId));
    }

    public HogwartsUser save(HogwartsUser newHogwartsUser) {
        newHogwartsUser.setPassword(this.passwordEncoder.encode(newHogwartsUser.getPassword()));
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .map(hogwartsUser -> new MyUserPrincipal(hogwartsUser))
                .orElseThrow(() -> new UsernameNotFoundException("username " + username +" not found"));
    }
}
