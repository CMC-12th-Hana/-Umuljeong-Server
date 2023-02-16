package cmc.hana.umuljeong.web.dto;

import lombok.*;

import java.time.LocalDateTime;

public class MemberResponseDto {
    public static class MemberListDto {
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProfileDto {
        private String name;
        private String role;
        private String companyName;
        private String staffRank;
        // todo : 사내 전화번호?....
        private String phoneNumber;
        private String email;
        private String staffNumber;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateDto {
        private Long memberId;
        private LocalDateTime createdAt;
    }
}
