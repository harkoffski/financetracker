package financetracker.model;

public enum TransactionType {
    INCOME("Доход"), // доход
    EXPENSE("Расход"); // расход
    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    @Override
    public String toString() {
        return displayName;
    }
}
