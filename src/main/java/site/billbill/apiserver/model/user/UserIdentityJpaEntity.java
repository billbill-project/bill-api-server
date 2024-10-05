package site.billbill.apiserver.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.billbill.apiserver.api.auth.dto.request.IdentityRequest;
import site.billbill.apiserver.model.BaseTime;

import java.time.LocalDate;


@Entity
@Table(name = "users_identity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserIdentityJpaEntity extends BaseTime {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(name = "birth", nullable = false)
    private LocalDate birth;
    @Column(name = "gender", nullable = false)
    private char gender;

    public static UserIdentityJpaEntity toJpaEntity(String userId, IdentityRequest request) {
        return new UserIdentityJpaEntity(
                userId,
                request.getName(),
                request.getPhoneNumber(),
                request.getBirth(),
                request.getGender()
        );
    }
}
