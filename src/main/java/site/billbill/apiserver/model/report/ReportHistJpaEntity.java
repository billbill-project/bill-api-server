package site.billbill.apiserver.model.report;

import jakarta.persistence.*;
import lombok.*;
import site.billbill.apiserver.common.converter.StringListConverter;
import site.billbill.apiserver.common.enums.base.ReportCode;
import site.billbill.apiserver.common.enums.base.ReportStatus;
import site.billbill.apiserver.common.enums.base.ReportType;
import site.billbill.apiserver.model.BaseTime;

import java.util.List;

@Entity
@Table(name = "report_hist")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportHistJpaEntity extends BaseTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_seq", nullable = false)
    private Long reportSeq;
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;
    @Column(name = "target_id", nullable = false)
    private String targetId;
    @Column(name = "reporter_id", nullable = false)
    private String reporterId;
    @Convert(converter = StringListConverter.class)
    @Column(name = "report_code", nullable = false)
    private List<String> reportCode;
    @Column(name = "description", nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false)
    private ReportStatus reportStatus = ReportStatus.REPORTED;
}
