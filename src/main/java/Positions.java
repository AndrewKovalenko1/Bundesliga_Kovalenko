public enum Positions {
    TORWART("Torwart"),
    VERTEIDIGER("Verteidiger"),
    MITTELFELD("Mittelfeld"),
    STURM("Sturm");

    private final String name;

    Positions(String name) {
        this.name = name;
    }

    public String zeigeName(){
        return this.name;
    }
}
