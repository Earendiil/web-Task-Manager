package taskmanager.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import taskmanager.entity.User;
import taskmanager.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
   private final UserRepository userRepository;
   
    public UserDetailsServiceImpl(UserRepository userRepository) {
	super();
	this.userRepository = userRepository;
}



	@Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }


}