package com.example.Student.Management.Security;

import com.example.Student.Management.Model.User;
import com.example.Student.Management.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    private final UserService us;
    public CustomUserDetailsService(UserService us)
    {
        this.us=us;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user=us.getUserByUsername(username);
        
        if(user==null)
        {
            throw new UsernameNotFoundException("User not found: "+username);
        }
        return new CustomUserDetails(user);
    }
}
