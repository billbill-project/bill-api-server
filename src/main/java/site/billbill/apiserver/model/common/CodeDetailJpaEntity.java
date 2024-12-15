package site.billbill.apiserver.model.common;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import site.billbill.apiserver.model.common.embedded.CodeDetailId;

@Entity
@Table(name = "code_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeDetailJpaEntity {
    @EmbeddedId
    private CodeDetailId id;

    @Column(name = "name")
    private String name;
    @Column(name = "seq")
    private Integer seq;
}
