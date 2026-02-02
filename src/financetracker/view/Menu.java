package financetracker.view;

import java.util.Scanner;

public class Menu {
    private Scanner scanner;

    public  Menu() {
        this.scanner = new Scanner(System.in);
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


}
