package com.nekretninanet.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SupportRequestResponseDto {

    @NotBlank(message = "Response must not be blank")
    @Size(max = 500, message = "Response cannot be longer than 500 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9 .,!?\\-()čćžšđČĆŽŠĐ]*$",
            message = "Response contains invalid characters"
    )
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
