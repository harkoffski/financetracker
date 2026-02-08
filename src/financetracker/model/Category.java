package financetracker.model;

import java.nio.charset.StandardCharsets;

public class Category {
    private final int id;
    private String name;
    private TransactionType type;

    public Category(int id, String name, TransactionType type) {
        if (id < 0){
            throw new IllegalArgumentException("ID должен быть неотрицательным числом");
        }
        this.id = id;
        setName(name);
        setType(type);
    }
    public Category(String name, TransactionType type) {
        this(0, name, type);
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public TransactionType getType() {
        return this.type;
    }


    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя категории не может быть пустым");
        }
        this.name = name.trim();
    }

    public void setType(TransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Тип не может быть null");
        }
        this.type = type;
    }

    public boolean isValid() {
        return id >= 0 && name != null && !name.trim().isEmpty() && name.length() <= 50 && type !=null;
    }

    public static Category createIncomeCategory(int id, String name) {
        return new Category(id, name, TransactionType.INCOME);

    }
    public static Category createExpenseCategory (int id, String name) {
        return new Category(id, name, TransactionType.EXPENSE);
    }

    public Category copy() {
        return new Category(this.id, this.name, this.type);
    }

    public Category copyWithNewId(int newId) {
        return new Category(newId, this.name, this.type);
    }

    public String toFileString() {
        return String.format("%d;%s;%s", id, name, type.name());
    }

    public static  Category fromFileString(String line) {
        String [] parts = line.split(";");
        if (parts.length != 3){
            throw new IllegalArgumentException("Некорректный формат строки: " + line);
        }
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        TransactionType type = TransactionType.valueOf(parts[2].trim());
        return new Category(id, name, type);
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    public  String getDisplayName() {
        return name + (isIncome() ? " (доход)" : " (расход)");
    }

    public boolean hasSameContent(Category other) {
        if (other == null) return false;
        return this.name.equals(other.name) && this.type == other.type;
    }

    @Override
    public String toString() {
        return String.format("Категория: %s (ID: %d, Тип: %s)", name, id, type.name());
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass() )
            return false;
        Category other = (Category) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public static final String FOOD = "Еда";
    public static final String TRANSPORT = "Транспорт";
    public static final String HEALTH = "Здоровье";
    public static final String ENTERTAINMENT = "Развлечения";
    public static final String UTILITIES = "Коммунальные услуги";
    public static final String COMMUNICATION = "Связь и интернет";
    public static final String HOUSEHOLD = "Быт";
    public static final String OTHER = "Другое";

    public static final String SALARY = "Зарплата";
    public static final String BONUS = "Премия";
    public static final String INVESTMENTS = "Проценты по вкладу";
    public static final String EXTRA_INCOME = "Дополнительный доход";

}
