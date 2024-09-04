package com.rulezero.playerconnector.handler;

import com.rulezero.playerconnector.controller.model.GamesData;
import com.rulezero.playerconnector.entity.Users;
import com.rulezero.playerconnector.service.GamesService;
import com.rulezero.playerconnector.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GameMenuHandler {

    @Autowired
    private GamesService gamesService;

    @Autowired
    private UserService userService;

    private Scanner scanner = new Scanner(System.in);

    public void process() {
        List<String> gameMenu = List.of(
                "1) Add a Game",
                "2) List Games",
                "3) Select a Game to Update",
                "4) Delete a Game",
                "0) Back"
        );

        boolean back = false;
        while (!back) {
            int selection = getUserSelection(gameMenu);
            switch (selection) {
                // TODO: Verify addGame
                case 1 -> addGame();
                // TODO: Verify listGames
                case 2 -> listGames();
                // TODO: Verify selectAndUpdateGame- Presently throwing a LAZY initialization exception, possibly connected to the players field, so a relational issue
                case 3 -> selectAndUpdateGame();
                // TODO: Verify deleteGame
                case 4 -> deleteGame();
                case 0 -> back = true;
                default -> System.out.println("Invalid selection");
            }
        }
    }

    private int getUserSelection(List<String> menu) {
        System.out.println("\nMake a selection:");
        menu.forEach(System.out::println);
        System.out.print("You selected: ");
        return Integer.parseInt(scanner.nextLine());
    }

    private void addGame() {
        System.out.println("Enter game name:");
        String gameName = scanner.nextLine();

        System.out.println("Enter game description");
        String gameDescription = scanner.nextLine();

        System.out.println("Enter Minimum number of Players");
        int minPlayers = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter Maximum players");
        int maxPlayers = Integer.parseInt(scanner.nextLine());

        GamesData newGame = new GamesData();
        newGame.setGameName(gameName);
        newGame.setGameDescription(gameDescription);
        newGame.setMinPlayers(minPlayers);
        newGame.setMaxPlayers(maxPlayers);
        newGame.setGameUsers(null);

        GamesData savedGame =gamesService.saveGame(newGame);
        System.out.println("Game attempting add" +savedGame);
    }

    private void listGames() {
        List<GamesData> games = gamesService.getAllGames();
        games.forEach(game -> System.out.println(game.getGameId() + ": " + game.getGameName()));
    }

    private void selectAndUpdateGame() {
        List<GamesData> games = gamesService.getAllGames();
        List<String> gameNames = games.stream()
                .map(game -> game.getGameId() + ": " + game.getGameName())
                .collect(Collectors.toList());

        System.out.println("Select a game to update:");
        for (int i = 0; i < gameNames.size(); i++) {
            System.out.println((i + 1) + ") " + gameNames.get(i));
        }

        int selection = Integer.parseInt(scanner.nextLine()) - 1;
        if (selection >= 0 && selection < games.size()) {
            GamesData selectedGame = games.get(selection);
            updateGame(selectedGame);
        } else {
            System.out.println("Invalid selection");
        }
    }

    private void updateGame(GamesData existingGame) {
        System.out.println("Updating game: " + existingGame.getGameName());

        System.out.println("Enter new game name (leave blank to keep current):");
        String gameName = scanner.nextLine();
        if (!gameName.isEmpty()) {
            existingGame.setGameName(gameName);
        }

        System.out.println("Enter new game description (leave blank to keep current):");
        String gameDescription = scanner.nextLine();
        if (!gameDescription.isEmpty()) {
            existingGame.setGameDescription(gameDescription);
        }

        System.out.println("Enter new minimum number of players (leave blank to keep current):");
        String minPlayersInput = scanner.nextLine();
        if (!minPlayersInput.isEmpty()) {
            existingGame.setMinPlayers(Integer.parseInt(minPlayersInput));
        }

        System.out.println("Enter new maximum number of players (leave blank to keep current):");
        String maxPlayersInput = scanner.nextLine();
        if (!maxPlayersInput.isEmpty()) {
            existingGame.setMaxPlayers(Integer.parseInt(maxPlayersInput));
        }
        // TODO: This is new and needs to be verified both structure and logic-wise. If it works, apply similar methods to other handler calls.
        System.out.println("Update players for the game? (enter player IDs separated by commas, leave blank to keep current):");
        String gameUserIdsInput = scanner.nextLine();
        if (!gameUserIdsInput.isEmpty()) {
            Set<Long> currentPlayers = existingGame.getUserIds();
            if (currentPlayers == null) {
                currentPlayers = new HashSet<>();
            }
            String[] newGameUserIds = gameUserIdsInput.split(",");
            for (String userId : newGameUserIds) {
                currentPlayers.add(Long.parseLong(userId));
            }
            existingGame.setUserIds(currentPlayers);
        }

        GamesData updatedGame = gamesService.patchGame(existingGame.getGameId(), existingGame);
        System.out.println("Game updated: " + updatedGame);
    }

    private void deleteGame() {
        System.out.println("Enter game ID to delete:");
        Long gameId = Long.parseLong(scanner.nextLine());

        gamesService.deleteGame(gameId);
        System.out.println("Game deleted.");
    }
}
