package site.billbill.apiserver.model.common.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CodeDetailId {
    @Column(name = "code")
    private String code;
    @Column(name = "group_code")
    private String groupCode;
}
