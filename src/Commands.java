import java.util.ArrayList;

/* This class used for checking valid commands. add any desired command to its constructor*/
public class Commands {
    private ArrayList<String> commands = new ArrayList<>();

    public Commands() {
        commands.add("AT+GETALL");
        commands.add("AT+SETVAL");
        commands.add("AT+PASS");
        commands.add("AT+DEL");
        commands.add("AT+REN");
        commands.add("AT+DELALL");
        commands.add("AT+ROUTER");
        commands.add("AT+GETVAL");
        commands.add("AT+NEWPASS");
    }

    public boolean isValidCommand(String command) {
        for (String e: commands) {
            if (command.startsWith(e)) {
                return true;
            }
        }
        return false;
    }
}
