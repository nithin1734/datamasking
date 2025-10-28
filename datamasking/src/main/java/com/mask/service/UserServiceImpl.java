package com.mask.service;

import com.mask.dto.UserRegistrationDto;
import com.mask.model.PasswordResetToken;
import com.mask.model.User;
import com.mask.repository.PasswordResetTokenRepository;
import com.mask.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    
    @Override
    public User registerUser(UserRegistrationDto registrationDto) throws Exception {
        
        // Check if email already exists
        if (existsByEmail(registrationDto.getEmail())) {
            throw new Exception("Email already registered");
        }
        
        // Check if mobile number already exists
        if (existsByMobileNumber(registrationDto.getMobileNumber())) {
            throw new Exception("Mobile number already registered");
        }
        
        // Check if passwords match
        if (!registrationDto.isPasswordMatching()) {
            throw new Exception("Passwords do not match");
        }
        
        // Validate password strength (optional)
        if (!isValidPassword(registrationDto.getPassword())) {
            throw new Exception("Password must contain at least 8 characters including uppercase, lowercase, number and special character");
        }
        
        // Create new user
        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setMobileNumber(registrationDto.getMobileNumber());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEnabled(true);
        
        return userRepository.save(user);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsByMobileNumber(String mobileNumber) {
        return userRepository.existsByMobileNumber(mobileNumber);
    }
    
    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        // Delete existing tokens for this user
        tokenRepository.deleteByUser(user);
        
        // Create new token with 1 hour expiry
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, expiryDate);
        tokenRepository.save(passwordResetToken);
    }
    
    @Override
    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findValidToken(token, LocalDateTime.now());
        return tokenOpt.isPresent();
    }
    
    @Override
    public User getUserByPasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findValidToken(token, LocalDateTime.now());
        if (tokenOpt.isPresent()) {
            return tokenOpt.get().getUser();
        }
        return null;
    }
    
    @Override
    public void changeUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    @Override
    public void invalidatePasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isPresent()) {
            PasswordResetToken resetToken = tokenOpt.get();
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);
        }
    }
    
    @Override
    public boolean isValidPassword(String password) {
        return pattern.matcher(password).matches();
    }
}