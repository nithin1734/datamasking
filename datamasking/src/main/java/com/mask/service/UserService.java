package com.mask.service;

import com.mask.dto.UserRegistrationDto;
import com.mask.model.User;
import java.util.Optional;

public interface UserService {
    
    User registerUser(UserRegistrationDto registrationDto) throws Exception;
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByMobileNumber(String mobileNumber);
    
    void createPasswordResetTokenForUser(User user, String token);
    
    boolean validatePasswordResetToken(String token);
    
    User getUserByPasswordResetToken(String token);
    
    void changeUserPassword(User user, String newPassword);
    
    void invalidatePasswordResetToken(String token);
    
    boolean isValidPassword(String password);
}