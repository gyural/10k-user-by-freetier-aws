package org.example.honorsparkingbe.service;

/**
 * 사용자 인증 객체 테스트
 * - 올바른 authId가 주어졌을 때, 올바른 CustomUserDetails를 반환하는지
 * - 잘못된 authId가 주어졌을 때, 올바른 CustomUserDetails를 반환하는지
 */
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.repository.UserRepository;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        // Mock UserRepository 생성
        userRepository = Mockito.mock(UserRepository.class);
        // CustomUserDetailsService 초기화
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Given
        String authId = "existingUser";
        MemberEntity mockUser = new MemberEntity();
        mockUser.setAuthId(authId);
        mockUser.setPassword("password123");
        mockUser.setRole(MemberRole.ROLE_USER);

        when(userRepository.findByAuthId(authId)).thenReturn(mockUser);

        // When
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(authId);

        // Then
        assertNotNull(userDetails);
        assertEquals(authId, userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
        verify(userRepository, times(1)).findByAuthId(authId);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        // Given
        String authId = "nonExistentUser";
        when(userRepository.findByAuthId(authId)).thenReturn(null);

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(authId);
        });

        assertEquals("User not found with authId: " + authId, exception.getMessage());
        verify(userRepository, times(1)).findByAuthId(authId);
    }
}
