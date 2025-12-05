package edu.fscj.cen4940.capstone.auth;

import edu.fscj.cen4940.capstone.entity.User;
import edu.fscj.cen4940.capstone.repository.UserRepository;
import edu.fscj.cen4940.capstone.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import java.security.MessageDigest;

@Service
@AllArgsConstructor
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UserPrincipal(user);
    }


    public User authenticate(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BadCredentialsException("Unauthorized");
        }
        boolean verified = verifyPasswordHash(password, user.getHash(), user.getSalt());
        if (!verified) throw new BadCredentialsException("Unauthorized");
        return user;
    }

    private boolean verifyPasswordHash(String password, byte[] storedHash, byte[] storedSalt) throws Exception {
        var computedHash = userService.hashPassword(password, storedSalt); // reuse UserService.hashPassword()
        return MessageDigest.isEqual(computedHash, storedHash);
    }
}
