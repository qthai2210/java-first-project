package com.example.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistRequestDto {
    @NotBlank(message = "Watchlist name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;
}
