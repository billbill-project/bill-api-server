package site.billbill.apiserver.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data
public class IdentityRequest {
    @Schema(description = "회원 본명", example = "홍길동")
    private String name;
    @Schema(description = "회원 전화번호", example = "010-1234-5678")
    private String phoneNumber;
    @Schema(description = "회원 성별", example = "M / F")
    private char gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "회원 생년월일", example = "yyyy-MM-dd")
    private LocalDate birth;
}
