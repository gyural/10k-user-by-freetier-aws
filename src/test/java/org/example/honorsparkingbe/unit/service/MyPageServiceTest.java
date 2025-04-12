// MyPageServiceTest.java
package org.example.honorsparkingbe.unit.service;

import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.CarType;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.dto.mypage.*;
import org.example.honorsparkingbe.repository.internal.CarRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.security.util.SecurityUtil;
import org.example.honorsparkingbe.service.MyPageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MyPageServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private MyPageService myPageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserInfo_shouldReturnUserInfo() {
        Long userId = 1L;
        CarEntity car = CarEntity.builder().carNumber("12가 1234").build();
        MemberEntity user = MemberEntity.builder()
                .id(userId)
                .userName("홍길동")
                .authId("hong")
                .phoneNumber("01012345678")
                .email("hong@test.com")
                .birthdayYear(1995)
                .birthday("0101")
                .loginPlatform(LoginPlatform.NORMAL)
                .carEntity(car)
                .build();

        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            when(memberRepository.findById(userId)).thenReturn(Optional.of(user));

            GetUserInfoResponseDTO dto = myPageService.getUserInfo();

            assertEquals("홍길동", dto.getUserName());
            assertEquals("12가 1234", dto.getCarNumber());
        }
    }

    @Test
    void updateUserInfo_shouldUpdatePhoneAndEmail() {
        Long userId = 1L;
        MemberEntity user = new MemberEntity();

        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            when(memberRepository.findById(userId)).thenReturn(Optional.of(user));

            UpdateUserInfoRequestDTO dto = new UpdateUserInfoRequestDTO("01099997777", "new@test.com");
            myPageService.updateUserInfo(dto);

            assertEquals("01099997777", user.getPhoneNumber());
            assertEquals("new@test.com", user.getEmail());
            verify(memberRepository).save(user);
        }
    }

    @Test
    void updateUserCarNumber_shouldUpdateWhenAfter30Days() {
        Long userId = 1L;
        CarEntity car = CarEntity.builder()
                .carNumber("33가 3333")
                .createdAt(LocalDateTime.now().minusDays(31))
                .build();
        MemberEntity user = new MemberEntity();
        user.setCarEntity(car);

        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            when(memberRepository.findById(userId)).thenReturn(Optional.of(user));

            UpdateUserCarNumberRequestDTO dto = new UpdateUserCarNumberRequestDTO("55가 5555");
            myPageService.updateUserCarNumber(dto);

            assertEquals("55가 5555", car.getCarNumber());
            verify(carRepository).save(car);
        }
    }

    @Test
    void updateUserCarNumber_shouldThrowIfWithin30Days() {
        Long userId = 1L;
        CarEntity car = CarEntity.builder().createdAt(LocalDateTime.now().minusDays(10)).build();
        MemberEntity user = new MemberEntity();
        user.setCarEntity(car);

        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            when(memberRepository.findById(userId)).thenReturn(Optional.of(user));

            UpdateUserCarNumberRequestDTO dto = new UpdateUserCarNumberRequestDTO("66가 6666");

            assertThrows(IllegalArgumentException.class, () -> myPageService.updateUserCarNumber(dto));
        }
    }

    @Test
    void changeUserPassword_shouldChangeIfLocalUser() {
        Long userId = 1L;
        MemberEntity user = new MemberEntity();

        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            mocked.when(SecurityUtil::getCurrentUserLoginPlatform).thenReturn(true);
            when(memberRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

            ChangeUserPasswordRequestDTO dto = new ChangeUserPasswordRequestDTO("newPass");
            myPageService.changeUserPassword(dto);

            assertEquals("encodedPass", user.getPassword());
            verify(memberRepository).save(user);
        }
    }

    @Test
    void changeUserPassword_shouldThrowIfSocialUser() {
        Long userId = 1L;
        MemberEntity user = new MemberEntity();

        try (MockedStatic<SecurityUtil> mocked = Mockito.mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            mocked.when(SecurityUtil::getCurrentUserLoginPlatform).thenReturn(false);
            when(memberRepository.findById(userId)).thenReturn(Optional.of(user));

            ChangeUserPasswordRequestDTO dto = new ChangeUserPasswordRequestDTO("newPass");

            assertThrows(IllegalStateException.class, () -> myPageService.changeUserPassword(dto));
        }
    }
}
