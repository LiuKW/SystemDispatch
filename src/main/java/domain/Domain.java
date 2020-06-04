package domain;

import manager.ProcessManager;
import resolver.CommandResolver;
import java.util.Scanner;

public class Domain {

    public static void main(String[] args) {
        ProcessManager processManager = ProcessManager.getInstance();
        CommandResolver commandResolver = new CommandResolver(processManager);
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");

        while(true)
            commandResolver.resolve(scanner.next());

    }
}
