package financetracker;

import financetracker.model.FileHandler;
import financetracker.model.FinanceManager;
import financetracker.view.ConsoleUI;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              ФИНАНСОВЫЙ ПОМОЩНИК (v1.0.0)                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();


        boolean isIntelliJ = System.getProperty("java.class.path").contains("idea_rt.jar");
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");



        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {

                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "chcp 65001");
                pb.inheritIO().start().waitFor();


                Thread.sleep(500);
            } catch (Exception e) {
                System.err.println("Не удалось настроить кодовую страницу. Используйте: chcp 65001");
            }
        }



        try {







            System.out.println("Инициализация приложения...");


            FileHandler fileHandler = new FileHandler("data/categories.txt", "data/transactions.txt");
            System.out.println("Файловый менеджер инициализирован");


            FinanceManager financeManager = new FinanceManager(fileHandler);
            System.out.println("Финансовый менеджер инициализирован");


            int categoriesCount = financeManager.getAllCategories().size();
            int transactionsCount = financeManager.getAllTransactions().size();
            double balance = financeManager.getTotalBalance();

            System.out.println("Загружено:");
            System.out.printf("   Категорий: %d\n", categoriesCount);
            System.out.printf("   Транзакций: %d\n", transactionsCount);
            System.out.printf("   Текущий баланс: %.2f руб.\n", balance);

            System.out.println("\nЗапуск интерфейса...");
            System.out.println("──────────────────────────────────────────────────────");


            ConsoleUI consoleUI = new ConsoleUI(financeManager);


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

            }
        }
    }
}