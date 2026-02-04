package financetracker.view;


import financetracker.model.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class ConsoleUI {
    private FinanceManager financeManager;
    private Menu menu;

    public ConsoleUI(FinanceManager financeManager) {
        this.financeManager = financeManager;
        this.menu = new Menu(financeManager);
    }

    public void start() {
        menu.showMessage("Добро пожаловать в Финансовый помощник!");

        boolean running = true;

        while (running) {
            int choice = menu.showMainMenu();

            switch (choice) {
                case Menu.ADD_TRANSACTION:
                    addTransactionFlow();
                    break;
                case Menu.VIEW_TRANSACTIONS:
                    viewTransactionsFlow();
                    break;
                case Menu.MANAGE_CATEGORIES:
                    manageCategoriesFlow();
                    break;
                case Menu.STATISTICS:
                    statisticsFlow();
                    break;
                case Menu.EXPORT_DATA:
                    exportFlow();
                    break;
                case Menu.EXIT:
                    running = false;
                    menu.showMessage("Спасибо за использование! До свидания!");
                    break;
                default:
                    menu.showError("Неверный выбор! Попробуйте снова.");
            }
        }
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return LocalDate.now();
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(dateString, formatter);

        } catch (DateTimeParseException e) {
            menu.showError("Неверный формат даты. Используется текущая дата.");
            return LocalDate.now();
        }
    }


    private void addTransactionFlow() {
        menu.clearScreen();

        int typeChoice = menu.showAddTransactionMenu();
        if (typeChoice == 0) return;

        TransactionType type = (typeChoice == 1) ? TransactionType.INCOME : TransactionType.EXPENSE;
        double amount = menu.requestAmount("Введите сумму: ");
        String description = menu.requestDescription("Введите описание (необязательно):");
        String dateInput = menu.requestDate("Введите дату: ");
        LocalDate date = parseDate(dateInput);

        List<Category> categories = financeManager.getCategoriesByType(type);
        if (categories.isEmpty()) {
            menu.showError("Нет доступных категорий. Сначала создайте категории.");
            return;
        }

        System.out.println("\n--- ВЫБЕРИТЕ КАТЕГОРИЮ ---");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, categories.get(i).getName());
        }
        System.out.printf("%d. Другое\n", categories.size() + 1);
        System.out.println("0. Назад");
        System.out.print("Ваш выбор: ");

        int categoryChoice = menu.readInt();
        if (categoryChoice == 0) return;

        Category selectedCategory;

        if (categoryChoice == categories.size() + 1) {
            selectedCategory = menu.handleOtherCategory(type, categories);
            if (selectedCategory == null) {
                menu.showError("Не удалось выбрать категорию.");
                return;
            }
            if (selectedCategory.getId() == 0) {
                financeManager.addCategory(selectedCategory.getName(), selectedCategory.getType());
                selectedCategory = financeManager.findCategoryByName(selectedCategory.getName());
            }
        } else if (categoryChoice > 0 && categoryChoice <= categories.size()) {
            selectedCategory = categories.get(categoryChoice - 1);
        } else {
            menu.showError("Неверный выбор категории.");
            return;
        }

        try {
            boolean success = financeManager.addTransaction(amount, selectedCategory, description, date);
            if (success) menu.showSuccess("Транзакция успешно добавлена!");
        } catch (IllegalArgumentException e) {
            menu.showError(e.getMessage());
        }
    }

    private void viewTransactionsFlow() {
        menu.clearScreen();
        int choice = menu.showViewTransactionsMenu();

        switch (choice) {
            case 1:
                List<Transaction> allTransactions = financeManager.getAllTransactions();
                menu.displayTransactions(allTransactions, "ВСЕ ТРАНЗАКЦИИ");
                break;
            case 2:
                String[] dateRange = menu.requestDateRange();
                LocalDate startDate = parseDate(dateRange[0]);
                LocalDate endDate = parseDate(dateRange[1]);
                List<Transaction> periodTransactions = financeManager.getTransactionsByDateRange(startDate, endDate);
                menu.displayTransactions(periodTransactions, String.format("ТРАНЗАКЦИИ С %s ПО %s",
                        startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
                break;
            case 3:
                List<Category> allCategories = financeManager.getAllCategories();
                int categoryId = menu.showCategorySelection(allCategories, "Выберите категорию:");
                if (categoryId != -1) {
                    List<Transaction> categoryTransactions = financeManager.getTransactionsByCategory(categoryId);
                    Category category = financeManager.getCategoryById(categoryId);
                    menu.displayTransactions(categoryTransactions, "ТРАНЗАКЦИИ ПО КАТЕГОРИИ: " + category.getName());
                }
                break;
            case 4:
                searchTransactionFlow();
                break;
            case 0:
                return;
            default:
                menu.showError("Неверный выбор!");

        }
        menu.showMessage("");
    }

    private void searchTransactionFlow() {
        System.out.println("\n--- ПОИСК ТРАНЗАКЦИИ ---");
        System.out.println("1. Поиск по ID");
        System.out.println("2. Поиск по описанию");
        System.out.println("0. Назад");
        System.out.print("Выберите вариант: ");

        int choice = menu.readInt();

        switch (choice) {
            case 1:
                System.out.print("Введите ID транзакции: ");
                int id = menu.readInt();
                Transaction transaction = financeManager.getTransactionById(id);
                if (transaction != null) {
                    System.out.println("\nНАЙДЕНА ТРАНЗАКЦИЯ:");
                    System.out.println(transaction.getDisplayString());
                } else {
                    menu.showError("Транзакция с ID " + id + " не найдена.");
                }
                break;
            case 2:
                System.out.println("Введите текст для поиска в описании: ");
                String searchText = menu.requestDescription("");
                List<Transaction> allTransactions = financeManager.getAllTransactions();
                List<Transaction> found = new java.util.ArrayList<>();
                for (Transaction t : allTransactions) {
                    if (t.getDescription().toLowerCase().contains(searchText.toLowerCase())) {
                        found.add(t);
                    }
                }
                menu.displayTransactions(found, "РЕЗУЛЬТАТЫ ПОИСКА: " + searchText);
                break;
            case 0:
                return;
        }
    }


    private void manageCategoriesFlow() {
        menu.clearScreen();
        int choice = menu.showCategoriesMenu();

        switch (choice) {
            case 1:
                List<Category> allCategories = financeManager.getAllCategories();
                System.out.println("\n--- ВСЕ КАТЕГОРИИ ---");
                if (allCategories.isEmpty()) {
                    System.out.println("Категории не найдены.");
                } else {
                    for (Category category : allCategories) {
                        System.out.println(category);
                    }
                    System.out.println("\nВсего категорий: " + allCategories.size());
                }
                break;

            case 2:
                System.out.println("\n--- ДОБАВЛЕНИЕ КАТЕГОРИИ ---");
                System.out.println("1. Категория доходов");
                System.out.println("2. Категория расходов");
                System.out.println("0. Назад");
                System.out.print("Выберите тип: ");
                int typeChoice = menu.readInt();
                if (typeChoice == 0) return;
                TransactionType type = (typeChoice == 1) ? TransactionType.INCOME : TransactionType.EXPENSE;
                String name = menu.requestCategoryName("Введите название категории: ");
                boolean added = financeManager.addCategory(name, type);
                if (added) {
                    menu.showSuccess("Категория '" + name + "' успешно добавлена!");
                } else {
                    menu.showError("Категория с таким названием уже существует!");
                }
                break;
            case 3:
                System.out.println("\n--- УДАЛЕНИЕ КАТЕГОРИИ ---");
                List<Category> categories = financeManager.getAllCategories();
                if (categories.isEmpty()) {
                    menu.showError("Нет категорий для удаления.");
                    break;
                }
                int categoryId = menu.showCategorySelection(categories, "Выберите категорию для удаления:");
                if (categoryId != -1) {
                    boolean removed = financeManager.removeCategory(categoryId);
                    if (removed) {
                        menu.showSuccess("Категория успешно удалена!");
                    } else {
                        menu.showError("Не удалось удалить категорию. Возможно, есть связанные транзакции.");
                    }
                }
                break;
            case 0:
                return;
            default:
                menu.showError("Неверный выбор!");
        }
        menu.showMessage("");
    }


    private void statisticsFlow() {
        menu.clearScreen();
        int choice = menu.showStatisticsMenu();

        switch (choice) {
            case 1:
                Map<String, Object> summary = financeManager.getFinancialSummary();
                menu.displayFinancialSummary(summary);
                break;
            case 2:
                System.out.println("\\nВведите год для статистики (например, 2025): ");
                int year = menu.readInt();
                Map<String, Map<TransactionType, Double>> monthlyStats = financeManager.getMonthlyStatistics(year);
                menu.displayMonthlyStatistics(monthlyStats, year);
                break;
            case 3:
                System.out.println("\n--- СТАТИСТИКА ПО КАТЕГОРИЯМ ---");
                System.out.println("1. Доходы по категориям");
                System.out.println("2. Расходы по категориям");
                System.out.println("0. Назад");
                System.out.print("Выберите тип: ");
                int statsChoice = menu.readInt();
                if (statsChoice == 0) return;
                TransactionType type = (statsChoice == 1) ? TransactionType.INCOME : TransactionType.EXPENSE;
                String[] dateRange = menu.requestDateRange();
                LocalDate startDate = parseDate(dateRange[0]);
                LocalDate endDate = parseDate(dateRange[1]);
                Map<String, Double> categoryStats = financeManager.getCategoryStatistics(startDate, endDate, type);
                String title = (type == TransactionType.INCOME) ?  "ДОХОДЫ ПО КАТЕГОРИЯМ" : "РАСХОДЫ ПО КАТЕГОРИЯМ";
                menu.displayCategoryStatistics(categoryStats, title, type);
                break;
            case 4:
                System.out.print("\nСколько крупнейших расходов показать?: ");
                int limit = menu.readInt();
                List<Transaction> topExpenses = financeManager.getTopExpenses(limit);
                menu.displayTransactions(topExpenses, "ТОП-" + limit + " КРУПНЕЙШИХ РАСХОДОВ");
                break;
            case 5:
                System.out.print("\nСколько последних транзакций показать?: ");
                int count = menu.readInt();
                List<Transaction> recentTransactions = financeManager.getRecentTransactions(count);
                menu.displayTransactions(recentTransactions, "ПОСЛЕДНИЕ " + count + " ТРАНЗАКЦИЙ");
                break;
            case 0:
                return;
            default:
                menu.showError("Неверный выбор!");
        }
        menu.showMessage("");
    }

    private void exportFlow() {
        menu.clearScreen();
        System.out.println("\n=== ЭКСПОРТ ДАННЫХ ===");
        System.out.println("1. Экспорт всех транзакций в CSV");
        System.out.println("2. Экспорт транзакций за период в CSV");
        System.out.println("3. Очистить все транзакции");
        System.out.println("0. Назад");
        System.out.print("Выберите пункт: ");

        int choice = menu.readInt();
        switch (choice) {
            case 1:
                System.out.print("Введите путь для сохранения (например: data/export.csv): ");
                String filePath = new java.util.Scanner(System.in).nextLine().trim();
                if (financeManager.exportTransactionsToCSV(filePath)) {
                    menu.showSuccess("Данные успешно экспортированы в " + filePath);
                } else {
                    menu.showError("Ошибка при экспорте данных.");
                }
                break;
            case 2:
                String[] dateRange = menu.requestDateRange();
                LocalDate startDate = parseDate(dateRange[0]);
                LocalDate endDate = parseDate(dateRange[1]);
                System.out.print("Введите путь для сохранения (например: data/export.csv): ");
                String path = new java.util.Scanner(System.in).nextLine().trim();
                if (financeManager.exportTransactionsToCSV(path, startDate, endDate)) {
                    menu.showSuccess("Данные успешно экспортированы в " + path);
                } else {
                    menu.showError("Ошибка при экспорте данных.");
                }
                break;
            case 3:
                boolean confirm = menu.confirm("Вы уверены, что хотите удалить ВСЕ транзакции?");
                if (confirm) {
                    financeManager.clearAllTransactions();
                    menu.showSuccess("Все транзакции успешно удалены!");

                } else {
                    menu.showMessage("Операция отменена.");
                }
                break;
            case 0:
                return;
            default:
                menu.showError("Неверный выбор!");
        }
    }


}
