package site.billbill.apiserver.model.sample;

import jakarta.persistence.*;
import lombok.*;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.common.converter.StringListConverter;
import site.billbill.apiserver.model.BaseTime;

import java.util.List;

@Entity
@Table(name = "sample")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SampleJpaEntity extends BaseTime {
    @Id @Column(name = "id", nullable = false)
    private String id;
    @Convert(converter = StringListConverter.class)
    @Column(name = "text_list", nullable = true)
    private List<String> textList;
    @Column(name = "yn", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean yn;
}
