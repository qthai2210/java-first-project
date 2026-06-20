package com.example.infrastructure.adapter.in.web;

import com.example.application.command.AddWatchlistStockCommand;
import com.example.application.command.UpdateWatchlistStockCommand;
import com.example.application.dto.WatchlistRequestDto;
import com.example.application.dto.WatchlistResponseDto;
import com.example.application.dto.WatchlistStockRequestDto;
import com.example.application.port.in.WatchlistServicePort;
import com.example.domain.model.Watchlist;
import com.example.infrastructure.mapper.WatchlistMapper;
import com.example.infrastructure.security.UserSecurity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/watchlists")
@Tag(name = "Watchlist Management", description = "Endpoints for managing user watchlists")
@SecurityRequirement(name = "bearerAuth")
public class WatchlistController {

    private final WatchlistServicePort watchlistServicePort;
    private final WatchlistMapper watchlistMapper;
    private final UserSecurity userSecurity;

    public WatchlistController(WatchlistServicePort watchlistServicePort,
                               WatchlistMapper watchlistMapper,
                               UserSecurity userSecurity) {
        this.watchlistServicePort = watchlistServicePort;
        this.watchlistMapper = watchlistMapper;
        this.userSecurity = userSecurity;
    }

    @PostMapping
    @Operation(summary = "Create a new watchlist")
    public ResponseEntity<WatchlistResponseDto> createWatchlist(@Valid @RequestBody WatchlistRequestDto request) {
        String email = userSecurity.getCurrentUserEmail();
        // Since we retrieve the user from the database, we need the user ID.
        // We'll get user id via security/email mapping.
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to create watchlist: {} for user ID: {}", request.getName(), userId);
        Watchlist watchlistDomain = watchlistMapper.mapToDomain(request);
        Watchlist created = watchlistServicePort.createWatchlist(userId, watchlistDomain);
        return new ResponseEntity<>(watchlistMapper.mapToDto(created), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all watchlists of the current user")
    public ResponseEntity<List<WatchlistResponseDto>> getUserWatchlists() {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to retrieve all watchlists for user ID: {}", userId);
        List<Watchlist> watchlists = watchlistServicePort.getUserWatchlists(userId);
        List<WatchlistResponseDto> response = watchlists.stream()
                .map(watchlistMapper::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a watchlist by ID")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isWatchlistOwner(#id)")
    public ResponseEntity<WatchlistResponseDto> getWatchlistById(@PathVariable Long id) {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to get watchlist: {} for user ID: {}", id, userId);
        Watchlist watchlist = watchlistServicePort.getWatchlistById(userId, id);
        return ResponseEntity.ok(watchlistMapper.mapToDto(watchlist));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a watchlist by ID")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isWatchlistOwner(#id)")
    public ResponseEntity<Void> deleteWatchlist(@PathVariable Long id) {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to delete watchlist: {} for user ID: {}", id, userId);
        watchlistServicePort.deleteWatchlist(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stocks")
    @Operation(summary = "Add a stock to a watchlist")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isWatchlistOwner(#id)")
    public ResponseEntity<WatchlistResponseDto> addStockToWatchlist(
            @PathVariable Long id,
            @Valid @RequestBody WatchlistStockRequestDto request) {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to add stock {} to watchlist: {}", request.getSymbol(), id);
        AddWatchlistStockCommand command = new AddWatchlistStockCommand(
                request.getSymbol(),
                request.getNotes(),
                request.getTargetPrice(),
                request.getStopLoss()
        );
        Watchlist updated = watchlistServicePort.addStockToWatchlist(userId, id, command);
        return ResponseEntity.ok(watchlistMapper.mapToDto(updated));
    }

    @DeleteMapping("/{id}/stocks/{symbol}")
    @Operation(summary = "Remove a stock from a watchlist")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isWatchlistOwner(#id)")
    public ResponseEntity<WatchlistResponseDto> removeStockFromWatchlist(
            @PathVariable Long id,
            @PathVariable String symbol) {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to remove stock {} from watchlist: {}", symbol, id);
        Watchlist updated = watchlistServicePort.removeStockFromWatchlist(userId, id, symbol);
        return ResponseEntity.ok(watchlistMapper.mapToDto(updated));
    }

    @PutMapping("/{id}/stocks/{symbol}")
    @Operation(summary = "Update notes, target price, or stop loss of a stock in a watchlist")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isWatchlistOwner(#id)")
    public ResponseEntity<WatchlistResponseDto> updateWatchlistStock(
            @PathVariable Long id,
            @PathVariable String symbol,
            @Valid @RequestBody WatchlistStockRequestDto request) {
        Long userId = userSecurity.getCurrentUserId();
        log.info("REST request to update stock {} details in watchlist: {}", symbol, id);
        UpdateWatchlistStockCommand command = new UpdateWatchlistStockCommand(
                symbol,
                request.getNotes(),
                request.getTargetPrice(),
                request.getStopLoss()
        );
        Watchlist updated = watchlistServicePort.updateWatchlistStock(userId, id, command);
        return ResponseEntity.ok(watchlistMapper.mapToDto(updated));
    }
}
