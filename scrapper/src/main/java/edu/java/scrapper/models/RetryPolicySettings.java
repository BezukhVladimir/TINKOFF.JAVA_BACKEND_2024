package edu.java.scrapper.models;

import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;


@Data
@Accessors(chain = true)
public class RetryPolicySettings {
    private RetryPolicy policy;
    private int count;
    private Set<HttpStatus> statuses;
}
