package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.domain.entity.ExpoEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.dto.expo.ExpoRequestDTO;
import org.example.honorsparkingbe.repository.internal.ExpoRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpoService {

    private final ExpoRepository expoRepository;
    private final MemberRepository memberRepository;

    public ExpoService(ExpoRepository expoRepository, MemberRepository memberRepository) {
        this.expoRepository = expoRepository;
        this.memberRepository = memberRepository;
    }

    public void pushToken(ExpoRequestDTO request) {
        String userId = request.getUserId();
        String pushToken = request.getPushToken();

        if(userId == null || pushToken == null) {
            throw new IllegalArgumentException("userId and pushToken must not be null");
        }

        ExpoEntity expoEntity = new ExpoEntity();
        expoEntity.setUserId(userId);
        expoEntity.setPushToken(pushToken);

        expoRepository.save(expoEntity);
    }

    @Transactional
    public void deleteToken(Long id){
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당되는 member 존재하지 않음"));

        expoRepository.deleteByUserId(member.getAuthId());
    }
}
