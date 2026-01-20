package financetracker.model;

import java.util.Objects;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Transaction {
    private final int id;
    private double amount;
    private Category category;
    private String description;
    private LocalDate date;


    public Transaction(int id, double amount, Category category, String description, LocalDate date) {
        if (id < 0) {
            throw new IllegalArgumentException("ID должен быть неотрицательным");
        }

        this.id = id;
        setAmount(amount);
        setCategory(category);
        setDescription(description);
        setDate(date);
    }

    public Transaction(double amount, Category category, String description, LocalDate date) {
        this(0, amount, category, description, date);

    }

    public Transaction(int id, double amount, Category category, LocalDate date) {
        this(id, amount, category, "", date);
    }

    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
        this.amount = amount;
    }

    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Категория не может быть null");
        }
        this.category = category;
    }

    public void setDescription(String description) {
        if (description == null) {
            this.description = "";
        } else {
            this.description = description.trim();
        }
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            this.date = LocalDate.now();
        } else if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата не может быть будущей");
        } else {
            this.date = date;
        }
    }

    public int getId() { return  id; }
    public double getAmount() { return amount; }
    public Category getCategory() { return category; }
    public  String getDescription() { return description; }
    public LocalDate getDate() { return date; }


    public  double getSignedAmount() {
        return category.isIncome() ? amount : -amount;
    }

    public int getYear() {
        return date.getYear();
    }


    public int getMonth() {
        return date.getMonthValue();
    }
    public String getFormatteddate() {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public boolean isValid() {
        return id >= 0 &&
                amount > 0 &&
                category != null &&
                category.isValid() &&
                date != null &&
                !date.isAfter(LocalDate.now());
    }

    public boolean isIncome() {
        return category.isIncome();
    }

    public boolean isExpense() {
        return category.isExpense();
    }

    public boolean hasSameContent(Transaction other) {
        if (other == null) return false;
        return Double.compare(this.amount, other.amount) == 0 &&
                this.category.equals(other.category) &&
                this.description.equals(other.description) &&
                this.date.equals(other.date);
    }

    public String getDisplayString() {
        String sign = isIncome() ? "+" : "-";
        String desc = description.isEmpty() ? "без описания" : description;
        return String.format("%s: %s%.2f руб. (%s) - %s",
                date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                sign,
                amount,
                desc,
                category.getName());
    }


    public Transaction copy () {
        return new Transaction(this.id, this.amount, this.category, this.description, this.date);
    }


    public Transaction copyWithNewId(int newId) {
        return new Transaction(newId, this.amount, this.category, this.description, this.date);
    }



    public String toFileString() {
        return String.format("%d|%.2f|%d|%s|%s",
                id,
                amount,
                category.getId(),
                description.replace("|", "\\|"),
                date.toString());
    }

    public static  Transaction fromFileString(String line, Map <Integer, Category> categoryMap) {
        try {
            String[] parts = line.split("\\|", -1);

            if (parts.length != 5) {
                throw new IllegalArgumentException("Некорректный формат строки: " + line);
            }
            int id = Integer.parseInt(parts[0]);
            double amount = Double.parseDouble(parts[1]);
            int categoryId = Integer.parseInt(parts[2]);
            String description = parts[3].replace("\\|", "|");
            LocalDate date = LocalDate.parse(parts[4]);

            Category category = categoryMap.get(categoryId);
            if (category == null) {

                System.err.println("Внимание: категория с ID= " + categoryId + " не найдена. Создана временная.");
                category = new Category(categoryId, "Неизвестная (ID:" + categoryId + ")", TransactionType.EXPENSE);
            }
            return new Transaction(id, amount, category, description, date);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка преобразования числа в строке: " + line, e);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Ошибка преобразования даты в строке: " + line, e);
        }
    }

    public static Transaction createIncome(int id, double amount, Category category, String description, LocalDate date) {
        if (category == null) {
            throw new IllegalArgumentException("Категория не должна быть null");
        }
        if (!category.isIncome()) {
            throw new IllegalArgumentException("Категория должна быть типом INCOME");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }
        return new Transaction(id, amount, category, description, date);
    }

    public static Transaction createExpense(int id, double amount, Category category, String description, LocalDate date) {
        if (category == null) {
            throw new IllegalArgumentException("Категория не должна быть null");
        }
        if (!category.isExpense()) {
            throw new IllegalArgumentException("Категория должна быть типом EXPENSE");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        return new Transaction(id, amount, category, description, date);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction other = (Transaction) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }


    @Override
    public String toString() {
        return String.format("Transaction{id=%d, amount=%.2f, category=%s, date=%s}",
                id, amount, category.getName(), date);
    }
}
