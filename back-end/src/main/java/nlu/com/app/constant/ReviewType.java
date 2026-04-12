package nlu.com.app.constant;


public enum ReviewType {
    BOOK("Sách"), COLLECTION("Bộ sách");
    private final String description;

    ReviewType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

