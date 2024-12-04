package site.billbill.apiserver.model.post;

import jakarta.persistence.*;
import lombok.*;

import site.billbill.apiserver.model.BaseTime;

import java.time.LocalDate;

@Entity
@Table(name = "search_keyword_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchKeywordStatsJpaEntity extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="keyword_seq")
    private long id;

    @Column(name="keyword",nullable = true)
    private String keyword;
    @Column(name = "search_count", nullable = false)
    private int searchCount;

}
