package com.example.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RefreshToken {
    private Long id;
    
    @EqualsAndHashCode.Include
    private String token;
    
    private Instant expiryDate;
    private User user;

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}
