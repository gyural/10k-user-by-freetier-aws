package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.dto.JoinDTO;
import org.example.honorsparkingbe.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class JoinServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private JoinService joinService;

    @Test
    public void testJoinProcess_Success() {
        JoinDTO joinDTO = new JoinDTO();
        joinDTO.setAuthId("testuser");
        joinDTO.setPassword("testpassword");

        when(userRepository.existsByAuthId("testuser")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("testpassword")).thenReturn("encodedPassword");

        joinService.joinProcess(joinDTO);

        verify(userRepository, times(1)).save(any(MemberEntity.class));
    }

    @Test
    public void testJoinProcess_DuplicateAuthId() {
        JoinDTO joinDTO = new JoinDTO();
        joinDTO.setAuthId("testuser");

        when(userRepository.existsByAuthId("testuser")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> joinService.joinProcess(joinDTO));

        assertEquals("authId= testuser already exists.", exception.getMessage());
    }
}
