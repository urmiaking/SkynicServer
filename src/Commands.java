import java.util.ArrayList;

public class Commands {
    private ArrayList<String> commands = new ArrayList<>();

    public Commands() {
        commands.add("AT+GETALL");
        commands.add("AT+SETVAL");
        commands.add("AT+PASS");
        commands.add("AT+GETALL");
        commands.add("AT+DEL");
        commands.add("AT+REN");
        commands.add("AT+DELALL");
    }

    public boolean isValidCommand(String command) {
        for (String e: commands) {
            if (command.startsWith(e)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOkPassword(String password) {
        return false;
    }
}
