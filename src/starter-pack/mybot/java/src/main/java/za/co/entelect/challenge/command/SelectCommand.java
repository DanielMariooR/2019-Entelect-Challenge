package za.co.entelect.challenge.command;

public class SelectCommand implements Command {
    private final int id;
    private final Command wormcommand;

    public SelectCommand(int id, Command wormcommand) {
        this.id = id;
        this.wormcommand = wormcommand;
    }

    @Override
    public String render() {
        return String.format("select %d; %s", id, wormcommand.render());
    }
}