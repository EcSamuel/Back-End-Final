package com.rulezero.playerconnector.controller;


import com.rulezero.playerconnector.controller.model.GamesData;
import com.rulezero.playerconnector.exception.ResourceNotFoundException;
import com.rulezero.playerconnector.service.GamesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing game-related operations.
 * This controller provides endpoints for creating, reading, updating, searching, and deleting games
 * in the system. It handles all game-related HTTP requests and delegates business logic to the GamesService.
 */
@RestController
@RequestMapping("/games")
@Slf4j
public class GamesController {
    @Autowired
    private GamesService gamesService;

    /**
     * Creates a new game entry in the system.
     *
     * @param gamesData The game data to be created
     * @return The created game data with assigned ID
     * @throws IllegalArgumentException if the game data is invalid
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GamesData createGame(@RequestBody GamesData gamesData) {
        log.info("Requesting Store Creation: {}", gamesData);
        return gamesService.saveGame(gamesData);
    }

    /**
     * Partially updates an existing game's information.
     * Only the provided fields in the request body will be updated.
     *
     * @param gameId The ID of the game to update
     * @param gamesData The partial game data containing only the fields to be updated
     * @return ResponseEntity containing the updated game data if successful
     *         or appropriate error status if the update fails
     */
    @PatchMapping("/{gameId}")
    public ResponseEntity<GamesData> partiallyUpdateGame(@PathVariable Long gameId, @RequestBody GamesData gamesData) {
        try {
            GamesData updatedGame = gamesService.patchGame(gameId, gamesData);
            return ResponseEntity.ok(updatedGame);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Retrieves a specific game by its ID.
     *
     * @param gameId The ID of the game to retrieve
     * @return ResponseEntity containing the game data if found,
     *         or NOT_FOUND status if the game doesn't exist
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<GamesData> getGameById(@PathVariable Long gameId) {
        try {
            GamesData gameData = gamesService.getGameById(gameId);
            return ResponseEntity.ok(gameData);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Searches for games based on a query string.
     * The search is performed on game names and returns matching results.
     *
     * @param query The search query string to match against game names
     * @return List of games matching the search query. Returns an empty list if the query is null or empty,
     *         or if no matches are found
     */
    @GetMapping("/search")
    public List<GamesData> searchGames(@RequestParam String query) {
        log.info("Requesting Game Search: {}", query);
        if (query == null || query.isEmpty()) {
            log.info("You must specify a query");
            return List.of(); // Return an empty list instead of null
        } else {
            return gamesService.searchGamesByName(query);
        }
    }

    /**
     * Deletes a specific game from the system.
     *
     * @param gameId The ID of the game to delete
     * @return ResponseEntity with:
     *         - NO_CONTENT status if deletion is successful
     *         - NOT_FOUND status if the game doesn't exist
     *         - INTERNAL_SERVER_ERROR status if deletion fails for other reasons
     */
    @DeleteMapping("/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
        log.info("Requesting Game Deletion: {}", gameId);
        try {
            gamesService.deleteGame(gameId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
