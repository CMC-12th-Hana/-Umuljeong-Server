package cmc.hana.umuljeong.converter;

import cmc.hana.umuljeong.domain.Member;
import cmc.hana.umuljeong.domain.enums.MemberRole;
import cmc.hana.umuljeong.exception.ErrorCode;
import cmc.hana.umuljeong.exception.MemberException;
import cmc.hana.umuljeong.repository.MemberRepository;
import cmc.hana.umuljeong.web.dto.AuthRequestDto;
import cmc.hana.umuljeong.web.dto.MemberRequestDto;
import cmc.hana.umuljeong.web.dto.MemberResponseDto;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class MemberConverter {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    private static PasswordEncoder staticPasswordEncoder;
    private static MemberRepository staticMemberRepository;


    @PostConstruct
    public void init() {
        staticPasswordEncoder = this.passwordEncoder;
        staticMemberRepository = this.memberRepository;
    }

    public static Member toMember(AuthRequestDto.JoinDto joinDto) {
        return Member.builder()
                .company(null)
                .memberRole(MemberRole.STAFF)
                .name(joinDto.getName())
                .email(joinDto.getEmail())
                .phoneNumber(joinDto.getPhoneNumber())
                .password(staticPasswordEncoder.encode(joinDto.getPassword()))
                .isEnabled(Boolean.TRUE)
                .build();
    }

    public static Member toMember(MemberRequestDto.CreateDto createDto) {
        return Member.builder()
                .company(null)
                .memberRole(MemberRole.STAFF)
                .name(createDto.getName())
                .email(null)
                .phoneNumber(createDto.getPhoneNumber())
                .password(null)
                .isEnabled(Boolean.FALSE)
                .build();
    }

    public static Member toMember(String phoneNumber) {
        return staticMemberRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public static MemberResponseDto.ProfileDto toProfileDto(Member member) {
        return MemberResponseDto.ProfileDto.builder()
                .name(member.getName())
                .role(member.getMemberRole().getDescription())
                .companyName(member.getCompany().getName())
                .staffRank("대리")
                .phoneNumber(member.getPhoneNumber())
                .email(member.getEmail())
                .staffNumber("121221")
                .build();
    }

    public static MemberResponseDto.CreateDto toCreateDto(Member createdMember) {
        return MemberResponseDto.CreateDto.builder()
                .memberId(createdMember.getId())
                .createdAt(createdMember.getCreatedAt())
                .build();
    }

}
