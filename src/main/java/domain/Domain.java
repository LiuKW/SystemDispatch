package domain;



import enums.StatusEnum;
import manager.ProcessManager;

import resolver.CommandResolver;

import java.io.IOException;
import java.util.Scanner;

public class Domain {


    public static void main(String[] args) {
        ProcessManager processManager = ProcessManager.getInstance();
        CommandResolver commandResolver = new CommandResolver(processManager);

        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");

        while(true)
        {
            String command = scanner.next();
            commandResolver.resolve(command);
        }
    }
}
