package financetracker.view;

import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsDisplay {

    // Текстовая "гистограмма" с символами █
    public static void displayBarChart(Map<String, Double> data, String title) {
        if (data == null || data.isEmpty()) {
            System.out.println("\n⚠ Нет данных для отображения");
            return;
        }

        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║ " + centerText(title, 54) + " ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝");

        // Находим максимальное значение для масштабирования
        double max = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);

        // Сортируем по убыванию и выводим столбцы
        data.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .forEach(entry -> {
                    int barLength = (int) (entry.getValue() / max * 40);
                    barLength = Math.max(1, barLength); // Минимум 1 символ

                    String bar = "█".repeat(barLength);
                    System.out.printf(" %-25s | %s %.2f руб.\n",
                            truncate(entry.getKey(), 25),
                            bar,
                            entry.getValue());
                });

        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    // Экспорт в простой JSON (без библиотек)
    public static String toJson(Map<String, Object> data) {
        return data.entrySet().stream()
                .map(entry -> {
                    String value = entry.getValue() instanceof String
                            ? "\"" + entry.getValue().toString().replace("\"", "\\\"") + "\""
                            : entry.getValue().toString();
                    return "  \"" + entry.getKey() + "\": " + value;
                })
                .collect(Collectors.joining(",\n", "{\n", "\n}"));
    }

    // Вспомогательные методы для красивого вывода
    private static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - text.length() - padding));
    }

    private static String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }
}