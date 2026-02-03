package financetracker.view;

import financetracker.model.Category;
import financetracker.model.FinanceManager;
import financetracker.model.Transaction;
import financetracker.model.TransactionType;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Menu {
    private Scanner scanner;
    private FinanceManager financeManager;

    public  Menu(FinanceManager financeManager) {

        this.scanner = new Scanner(System.in);
        this.financeManager = financeManager;
    }

    public static final int MAIN_MENU = 0;
    public static final int ADD_TRANSACTION = 1;
    public static final int VIEW_TRANSACTION = 2;
    public static final int MANAGE_CATEGORIES = 3;
    public static final int STATISTICS = 4;
    public static final int  EXPORT_DATA = 5;
    public static final int EXIT = 0;

    public static final int ADD_INCOME = 11;
    public static final int ADD_EXPENSE = 12;
    public static final int BACK = 99;

    public int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число:");
            }
        }
    }


    public int showMainMenu() {
        System.out.println("\n--- ФИНАНСОВЫЙ ПОМОЩНИК ---");
        System.out.println("1. Добавить транзакцию");
        System.out.println("2. Посмотреть транзакцию");
        System.out.println("3. Управление категориями");
        System.out.println("4. Статистика и отчёты");
        System.out.println("5. Экспорт данных");
        System.out.println("0. Выход");
        System.out.println("\nВыберите пункт: ");

        return readInt();
    }

    public int showAddTransactionMenu() {
        System.out.println("\n--- ДОБАВЛЕНИЕ ТРАНЗАКЦИИ ---");
        System.out.println("1. Доход");
        System.out.println("2. Расход");
        System.out.println("0. Назад");
        System.out.println("\nВыберите тип: ");

        return readInt();
    }

    public int showCategoriesMenu() {
        System.out.println("\n=== УПРАВЛЕНИЕ КАТЕГОРИЯМИ ===");
        System.out.println("1. Просмотреть все категории");
        System.out.println("2. Добавить категорию");
        System.out.println("3. Удалить категорию");
        System.out.println("0. Назад");
        System.out.print("\nВыберите пункт: ");

        return readInt();
    }

    public int showStatisticsMenu() {
        System.out.println("\n=== СТАТИСТИКА И ОТЧЕТЫ ===");
        System.out.println("1. Общая статистика");
        System.out.println("2. Статистика по месяцам");
        System.out.println("3. Статистика по категориям");
        System.out.println("4. Крупнейшие расходы");
        System.out.println("5. Последние транзакции");
        System.out.println("0. Назад");
        System.out.print("\nВыберите пункт: ");

        return readInt();
    }

    public int showViewTransactionsMenu() {
        System.out.println("\n=== ПРОСМОТР ТРАНЗАКЦИЙ ===");
        System.out.println("1. Все транзакции");
        System.out.println("2. Транзакции за период");
        System.out.println("3. Транзакции по категории");
        System.out.println("4. Поиск транзакции");
        System.out.println("0. Назад");
        System.out.print("\nВыберите пункт: ");

        return  readInt();
    }

    public double requestAmount(String message) {
        while (true) {

            try {
                System.out.print(message);
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("Сумма не может быть пустой!");
                    continue;
                }

                double amount = Double.parseDouble(input);

                if (amount <= 0) {
                    System.out.println("Сумма должна быть положительной!");
                    continue;
                }


                return  amount;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число (например: 5000.55)");
                continue;
            }

        }
    }

    public String requestDescription(String message) {
        System.out.println(message);
        return scanner.nextLine().trim();
    }

    public String requestDate(String message) {
        System.out.print(message + " (дд.мм.гггг или Enter для сегодняшней даты): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return null;
        }

        return input;
    }

    public String requestCategoryName(String message) {
        while (true) {
            System.out.println(message);
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Название категории не может быть пустым!");
                continue;
            }

            if (name.length() > 50) {
                System.out.println("Название слишком длинное (максимум 50 символов)");
                continue;
            }

            return name;
        }
    }


    public  String[] requestDateRange() {
        String[] range = new String[2];

        System.out.println("\nВведите период:");
        range[0] = requestDate("Начальная дата");
        range[1] = requestDate("Конечная дата");

        return range;
    }


    public int showCategorySelection(List<Category> categories, String message) {
        System.out.println("\n" + message);

        if (categories.isEmpty()) {
            System.out.println("Нет допустимых категорий.");
            return  -1;
        }

        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, categories.get(i).getName());
        }

        System.out.println("0. Назад");

        while (true) {
            System.out.println("\nВыберите категорию: ");
            String input = scanner.nextLine().trim();

            try {
                int choise = Integer.parseInt(input);

                if (choise == 0) {
                    return -1;
                }

                if (choise >= 1 && choise <= categories.size()) {
                    return categories.get(choise - 1).getId();
                } else {
                    System.out.println("Неверный выбор. Введите число от 0 до " + categories.size());
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число!");
            }
        }
    }


    public  void displayTransactions(List<Transaction> transactions, String title) {
        System.out.println("\n--- " + title + " ---");

        if(transactions.isEmpty()) {
            System.out.println("Транзакции не найдены.");
            return;
        }

        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction transaction : transactions) {
            System.out.println(transaction.getDisplayString());

            if (transaction.isIncome()) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpense+= transaction.getAmount();
            }
        }

        System.out.println("\n--- ИТОГО ---");
        System.out.printf("Доходы: +%.2f руб.\n", totalIncome);
        System.out.printf("Расходы: -%.2f руб.\n", totalExpense);
        System.out.printf("Баланс: %.2f руб.\n", totalIncome - totalExpense);
    }

    public void displayCategoryStatistics(Map<String, Double> statistics, String title, TransactionType type) {
        System.out.println("\n--- " + title + " ---");

        if (statistics.isEmpty()) {
            System.out.println("Данные отсутствуют");
            return;
        }

        double total = 0;

        statistics.entrySet().stream().sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())).forEach(entry -> {
            System.out.printf("%-25s: %8.2f руб.\n", entry.getKey(), entry.getValue());
        });

        total = statistics.values().stream().mapToDouble(Double::doubleValue).sum();

        System.out.printf("\nОбщая сумма: %.2f руб.\n", total);
    }

    public void displayMonthlyStatistics(Map<String, Map<TransactionType, Double>> monthlyStats, int year) {
        System.out.println("\n--- СТАТИСТИКА ЗА " + year + " ГОД ---");

        if (monthlyStats.isEmpty()) {
            System.out.println("Данные отсутствуют.");
            return;
        }

        System.out.println("Месяц      |   Доходы   |  Расходы   |   Баланс");
        System.out.println("-----------|------------|------------|------------");

        for(Map.Entry<String, Map<TransactionType, Double>> entry : monthlyStats.entrySet()) {
            String month = entry.getKey();
            double income = entry.getValue().getOrDefault(TransactionType.INCOME, 0.0);
            double expense = entry.getValue().getOrDefault(TransactionType.EXPENSE, 0.0);
            double balance = income - expense;

            System.out.printf("%-10s | %10.2f | %10.2f | %10.2f\n",
                    month, income, expense, balance);
        }
    }

    public boolean confirm(String message) {
        System.out.print(message + " (да/нет): ");
        String input = scanner.nextLine().trim().toLowerCase();

        return input.equals("да") || input.equals("д") || input.equals("y") || input.equals("yes");
    }

    public void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }


    public void showMessage(String message) {
        System.out.println("\n" + message);
        System.out.println("Нажмите Enter для продолжения...");
        scanner.nextLine();
    }

    public void showError(String error) {
        System.out.println("\n Ошибка" + error);
        System.out.println("Нажмите Enter для продолжения...");
        scanner.nextLine();
    }

    public void showSuccess(String message) {
        System.out.println("\n•" + message);
        System.out.println("Нажмите Enter для продолжения...");
        scanner.nextLine();
    }

    public void displayFinancialSummary(Map<String, Object> summary) {
        System.out.println("\n=== ФИНАНСОВАЯ СВОДКА ===");
        System.out.println("Общее количество транзакций: " + summary.get("totalTransactions"));
        System.out.println("Количество категорий: " + summary.get("totalCategories"));
        System.out.println("Общий баланс: " + summary.get("totalBalance") + " руб.");
        System.out.println("\n--- Текущий месяц ---");
        System.out.println("Доходы: " + summary.get("currentMonthIncome") + " руб.");
        System.out.println("Расходы: " + summary.get("currentMonthExpenses") + " руб.");
        System.out.println("Баланс: " + summary.get("currentMonthBalance") + " руб.");
        System.out.println("\n--- Распределение ---");
        System.out.println("Количество доходов: " + summary.get("incomeCount"));
        System.out.println("Количество расходов: " + summary.get("expenseCount"));
    }
    public  Category handleOtherCategory(TransactionType type, List<Category> existingCategories) {
        System.out.println("\n--- ДОБАВЛЕНИЕ НОВОЙ КАТЕГОРИИ ---");

        if (!existingCategories.isEmpty()) {
            System.out.println("Существующие категории:");
            for (Category cat : existingCategories) {
                System.out.println(" •" + cat.getName());
            }
            System.out.println();
        }

        System.out.println("1. Выбрать из существующих");
        System.out.println("2. Создать новую категорию");
        System.out.print("Ваш выбор: ");

        int choice = readInt();

        switch (choice) {
            case 1:
                int categoryId = showCategorySelection(existingCategories, "Выберите категорию из списка: ");

                if (categoryId == -1) {
                    return null;
                }

                return financeManager.getCategoryById(categoryId);

            case 2:
                String categoryName = requestCategoryName("Введите название новой категории: ");

                for (Category cat : existingCategories) {
                    if (cat.getName().equalsIgnoreCase(categoryName)) {
                        System.out.println("Категория с таким названием уже существует!");
                        return null;
                    }
                }

                return new Category(categoryName, type);

            default:
                System.out.println("Неверный выбор.");
                return null;
        }
    }
}
