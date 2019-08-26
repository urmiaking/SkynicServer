import java.util.ArrayList;

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

        //AT+NEWPASS new pass ok change password of tbl_phone
        //in the php file if the hub is deleted, the phone table must be remove too with that serial
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
