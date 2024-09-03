package com.rulezero.playerconnector;

import com.rulezero.playerconnector.handler.AvailabilityMenuHandler;
import com.rulezero.playerconnector.handler.GameMenuHandler;
import com.rulezero.playerconnector.handler.UserMenuHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class MenuCLI {

    @Autowired
    private AvailabilityMenuHandler availabilityMenuHandler;

    @Autowired
    private UserMenuHandler userMenuHandler;

    @Autowired
    private GameMenuHandler gameMenuHandler;

    private List<String> mainMenu = List.of(
            "1) Users",
            "2) Games",
            "3) Availability",
            "0) Quit"
    );

    private Scanner scanner = new Scanner(System.in);

    public void processUserSelections() {
        boolean done = false;

        while (!done) {
            int selection = getUserSelection(mainMenu);
            switch (selection) {
                case 1 -> userMenuHandler.process();
                case 2 -> gameMenuHandler.process();
                case 3 -> availabilityMenuHandler.process();
                case 0 -> done = true;
                default -> System.out.println("Invalid selection");
            }
        }
        System.out.println("Exiting the application.");
    }

    private int getUserSelection(List<String> menu) {
        System.out.println("\nMake a selection:");
        menu.forEach(System.out::println);
        System.out.println("You selected ");
        return Integer.parseInt(scanner.nextLine());
    }
}
