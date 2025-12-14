package com.nekretninanet.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class SupportRequestResponseDto {

    @NotBlank(message = "Response must not be blank")
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
