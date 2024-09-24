package site.billbill.apiserver.api.sample.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class SamplePostRequest {
    private List<String> textList;
}
