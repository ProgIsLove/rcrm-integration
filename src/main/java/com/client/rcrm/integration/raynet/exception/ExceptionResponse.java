package com.client.rcrm.integration.raynet.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {

    private List<ExceptionBody> errors;
    private Set<String> validationErrors;

    @Getter
    @Setter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ExceptionBody {
        private String title;
        private String details;
    }
}
