package nlu.com.app.constant;


public enum EGenre {
    COMEDY("Comedy"),
    FANTASY("Fantasy"),
    SHOUNEN("Shounen"),
    ACTION("Action"),
    ADVENTURE("Adventure"),
    DRAMA("Drama"),
    SCI_FI("Sci Fi"),
    SUPERNATURAL("Supernatural");
    private final String description;

    EGenre(String description) {
        this.description = description;
    }

    public static EGenre getGenreByDescription(String text) {
        for (EGenre e : EGenre.values()) {
            if (e.getDescription().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getGenreByDescription("Supernatural"));
    }

    public String getDescription() {
        return description;
    }
}
