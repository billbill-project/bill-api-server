package site.billbill.apiserver.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import site.billbill.apiserver.common.enums.user.identity.IdentityVerificationOperator;

@Data
public class IdentityVerificationRequest {
    @Schema(description = "회원 본명", example = "홍길동")
    private String name;
    @Schema(description = "회원 전화번호", example = "010-1234-5678")
    private String phoneNumber;
    @Schema(description = "주민번호 앞 7자리", example = "0001013")
    private String identityNumber;
    @Enumerated(EnumType.STRING)
    @Schema(description = "본인인증 통신사", example = "SKT")
    private IdentityVerificationOperator operator;
}
