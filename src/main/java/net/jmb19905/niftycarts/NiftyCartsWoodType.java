package net.jmb19905.niftycarts;

public enum NiftyCartsWoodType {

    OAK("oak", "Oak", "log"),
    SPRUCE("spruce", "Spruce", "log"),
    DARK_OAK("dark_oak", "Dark Oak", "log"),
    BIRCH("birch", "Birch", "log"),
    JUNGLE("jungle", "Jungle", "log"),
    ACACIA("acacia", "Acacia", "log"),
    MANGROVE("mangrove", "Mangrove", "log"),
    CHERRY("cherry", "Cherry", "log"),
    BAMBOO("bamboo", "Bamboo", "block"),
    WARPED("warped", "Warped", "stem"),
    CRIMSON("crimson", "Crimson", "stem");

    private final String id;
    private final String name;
    private final String logName;

    NiftyCartsWoodType(String id, String name, String logName) {
        this.id = id;
        this.name = name;
        this.logName = logName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogName() {
        return logName;
    }

    public static NiftyCartsWoodType getFromId(String id) {
        return valueOf(id.toUpperCase());
    }

}
