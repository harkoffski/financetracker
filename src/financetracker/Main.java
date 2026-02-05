package financetracker;

import financetracker.model.FileHandler;
import financetracker.model.FinanceManager;
import financetracker.view.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              ФИНАНСОВЫЙ ПОМОЩНИК (v1.0.0)                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            // Показываем информацию о запуске
            System.out.println("Инициализация приложения...");

            // Создаем обработчик файлов
            FileHandler fileHandler = new FileHandler("data/categories.txt", "data/transactions.txt");
            System.out.println("Файловый менеджер инициализирован");

            // Создаем менеджер финансов
            FinanceManager financeManager = new FinanceManager(fileHandler);
            System.out.println("Финансовый менеджер инициализирован");

            // Загружаем статистику
            int categoriesCount = financeManager.getAllCategories().size();
            int transactionsCount = financeManager.getAllTransactions().size();
            double balance = financeManager.getTotalBalance();

            System.out.println("Загружено:");
            System.out.printf("   Категорий: %d\n", categoriesCount);
            System.out.printf("   Транзакций: %d\n", transactionsCount);
            System.out.printf("   Текущий баланс: %.2f руб.\n", balance);

            System.out.println("\nЗапуск интерфейса...");
            System.out.println("──────────────────────────────────────────────────────");

            // Создаем консольный интерфейс
            ConsoleUI consoleUI = new ConsoleUI(financeManager);

            // Запускаем приложение
            consoleUI.start();
            System.out.println("Приложение завершено. Все данные сохранены.");

        } catch (Exception e) {
            System.err.println("\nКритическая ошибка при запуске приложения:");
            System.err.println("   " + e.getMessage());

            System.out.println("\n Возможные решения:");
            System.out.println("   1. Проверьте, что папка 'data' существует в директории проекта");
            System.out.println("   2. Проверьте права доступа к файлам");
            System.out.println("   3. Удалите файлы data/categories.txt и data/transactions.txt для пересоздания");

            System.out.print("\nНажмите Enter для выхода...");
            try {
                System.in.read();
            } catch (Exception ignored) {
                // Игнорируем ошибки при выходе
            }
        }
    }
}