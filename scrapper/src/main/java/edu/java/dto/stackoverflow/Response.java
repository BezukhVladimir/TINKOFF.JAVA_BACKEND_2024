// https://api.stackexchange.com/docs/answers-on-questions#pagesize=1&order=desc&sort=activity&ids=1732348&filter=default&site=stackoverflow
// https://api.stackexchange.com/docs/types/answer

package edu.java.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    private Owner owner;

    @JsonProperty("last_activity_date")
    private OffsetDateTime lastActivityDate;

    @JsonProperty("answer_id")
    private Long answerId;

    @JsonProperty("question_id")
    private Long questionId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Owner {
        @JsonProperty("display_name")
        private String displayName;
    }
}
