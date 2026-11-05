package edu.java.app.files;

import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Класс для работы с текстовыми файлами по методическим указаниям (Лабораторная работа № 5).
 * <p>
 * Обеспечивает сохранение и построчную загрузку данных о музыкальных группах
 * с использованием буферизованных символьных потоков ввода-вывода.
 *
 * @author Оганнисян Э. А.
 * @version 1.5
 */
public class TextFileManager {

    /** Путь к текущему открытому или сохраненному файлу */
    private static String currentFilePath = null;

    /**
     * Быстрое сохранение: если файл уже был открыт или сохранен,
     * перезаписывает его без повторного вызова диалогового окна.
     * Если файл новый — вызывает метод saveAs.
     *
     * @param frame родительское окно
     * @param model модель таблицы
     */
    public static void save(JFrame frame, DefaultTableModel model) {
        if (currentFilePath == null) {
            saveAs(frame, model);
        } else {
            writeToFile(frame, currentFilePath, model);
        }
    }

    /**
     * Сохранение данных в новый файл с вызовом диалогового окна FileDialog.
     *
     * @param frame родительское окно
     * @param model модель таблицы
     */
    public static void saveAs(JFrame frame, DefaultTableModel model) {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Таблица пуста! Нечего сохранять.",
                    "Предупреждение",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Диалоговое окно сохранения 
        FileDialog saveDialog = new FileDialog(frame, "Сохранить как...", FileDialog.SAVE);
        saveDialog.setFile("*.txt");
        saveDialog.setVisible(true);

        String dir = saveDialog.getDirectory();
        String file = saveDialog.getFile();

        if (dir == null || file == null) {
            return; // Пользователь нажал «Отмена»
        }

        if (!file.toLowerCase().endsWith(".txt")) {
            file += ".txt";
        }

        currentFilePath = dir + file;
        writeToFile(frame, currentFilePath, model);
    }

    /**
     * Построчная запись данных таблицы в текстовый файл .
     * Сохраняет значащие колонки с 1 по 6 (иконка, имя, рейтинг, хит, год, лидер).
     *
     * @param frame     родительское окно
     * @param fullPath  полный путь к файлу
     * @param model     модель таблицы
     */
    private static void writeToFile(JFrame frame, String fullPath, DefaultTableModel model) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))) {
            int rowCount = model.getRowCount();

            for (int i = 0; i < rowCount; i++) {
                // Колонку 0 (№) и колонку 7 (кнопка) пропускаем
                for (int j = 1; j <= 6; j++) {
                    Object val = model.getValueAt(i, j);
                    writer.write(val != null ? val.toString().trim() : "");
                    writer.write("\n"); // Перевод каретки
                }
            }

            File f = new File(fullPath);
            frame.setTitle("Music group list — " + f.getName());

            JOptionPane.showMessageDialog(
                    frame,
                    "Данные успешно сохранены в файл:\n" + f.getName(),
                    "Сохранение",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    frame,
                    "Ошибка при записи в файл: " + e.getMessage(),
                    "Ошибка ввода-вывода",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Построчное чтение данных из текстового файла в таблицу .
     *
     * @param frame родительское окно
     * @param model модель таблицы
     */
    public static void importData(JFrame frame, DefaultTableModel model) {
        // Диалоговое окно загрузки (п. 5.2 методички)
        FileDialog loadDialog = new FileDialog(frame, "Импорт данных", FileDialog.LOAD);
        loadDialog.setFile("*.txt");
        loadDialog.setVisible(true);

        String dir = loadDialog.getDirectory();
        String file = loadDialog.getFile();

        if (dir == null || file == null) {
            return;
        }

        currentFilePath = dir + file;

        try (BufferedReader reader = new BufferedReader(new FileReader(currentFilePath))) {
            // Очистка таблицы перед загрузкой 
            while (model.getRowCount() > 0) {
                model.removeRow(0);
            }

            String line;
            int counter = 1;

            // Каждая группа в файле занимает строго 6 строк подряд
            while ((line = reader.readLine()) != null) {
                int num = counter++;
                String icon = line;
                String name = reader.readLine();
                String rateStr = reader.readLine();
                String hit = reader.readLine();
                String yearStr = reader.readLine();
                String leader = reader.readLine();

                int rate = 0;
                try {
                    if (rateStr != null && !rateStr.trim().isEmpty()) {
                        rate = Integer.parseInt(rateStr.trim());
                    }
                } catch (NumberFormatException ignored) {}

                int year = 2000;
                try {
                    if (yearStr != null && !yearStr.trim().isEmpty()) {
                        year = Integer.parseInt(yearStr.trim());
                    }
                } catch (NumberFormatException ignored) {}

                model.addRow(new Object[]{
                        num,
                        icon != null ? icon : "",
                        name != null ? name : "",
                        rate,
                        hit != null ? hit : "",
                        year,
                        leader != null ? leader : "",
                        "Подробнее"
                });
            }

            File f = new File(currentFilePath);
            frame.setTitle("Music group list — " + f.getName());

            JOptionPane.showMessageDialog(
                    frame,
                    "Данные успешно импортированы из файла:\n" + f.getName(),
                    "Импорт",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    frame,
                    "Файл не найден:\n" + currentFilePath,
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    frame,
                    "Ошибка при чтении файла: " + e.getMessage(),
                    "Ошибка ввода-вывода",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

/**
 * Сохраняет таблицу в указанный TXT-файл.
 */
public static boolean saveToFile(
        JFrame frame,
        DefaultTableModel model,
        File file
) {
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(
                frame,
                "Таблица пуста. Нечего сохранять!",
                "Предупреждение",
                JOptionPane.WARNING_MESSAGE
        );
        return false;
    }

    try (java.io.BufferedWriter writer =
                 new java.io.BufferedWriter(
                         new java.io.FileWriter(file)
                 )) {

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 1; j <= 6; j++) {
                Object value = model.getValueAt(i, j);

                writer.write(value == null ? "" : value.toString());
                writer.newLine();
            }
        }

        currentFilePath = file.getAbsolutePath();
        frame.setTitle("Music group list - " + file.getName());

        JOptionPane.showMessageDialog(
                frame,
                "Данные успешно сохранены в TXT-файл!",
                "Сохранение",
                JOptionPane.INFORMATION_MESSAGE
        );

        return true;

    } catch (java.io.IOException e) {
        JOptionPane.showMessageDialog(
                frame,
                "Ошибка сохранения TXT-файла:\n" + e.getMessage(),
                "Ошибка",
                JOptionPane.ERROR_MESSAGE
        );

        return false;
    }
}

/**
 * Загружает данные из указанного TXT-файла.
 */
public static boolean importData(
        JFrame frame,
        DefaultTableModel model,
        File file
) {
    java.util.List<Object[]> importedRows =
            new java.util.ArrayList<>();

    try (java.io.BufferedReader reader =
                 new java.io.BufferedReader(
                         new java.io.FileReader(file)
                 )) {

        String icon;

        while ((icon = reader.readLine()) != null) {
            String name = reader.readLine();
            String rateText = reader.readLine();
            String hit = reader.readLine();
            String yearText = reader.readLine();
            String leader = reader.readLine();

            if (name == null || rateText == null
                    || hit == null || yearText == null
                    || leader == null) {
                throw new java.io.IOException(
                        "Файл содержит неполную запись группы."
                );
            }

            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Название группы не может быть пустым."
                );
            }

            int rate = Integer.parseInt(rateText.trim());
            int year = Integer.parseInt(yearText.trim());

            if (year < 1900 || year > 2026) {
                throw new IllegalArgumentException(
                        "Год основания должен быть в диапазоне 1900–2026."
                );
            }

            importedRows.add(new Object[]{
                    importedRows.size() + 1,
                    icon,
                    name,
                    rate,
                    hit,
                    year,
                    leader,
                    "Подробнее"
            });
        }

        model.setRowCount(0);

        for (Object[] row : importedRows) {
            model.addRow(row);
        }

        currentFilePath = file.getAbsolutePath();
        frame.setTitle("Music group list - " + file.getName());

        JOptionPane.showMessageDialog(
                frame,
                "Данные успешно загружены из TXT-файла!",
                "Импорт",
                JOptionPane.INFORMATION_MESSAGE
        );

        return true;

    } catch (java.io.IOException | IllegalArgumentException e) {
        JOptionPane.showMessageDialog(
                frame,
                "Ошибка чтения TXT-файла:\n" + e.getMessage(),
                "Ошибка импорта",
                JOptionPane.ERROR_MESSAGE
        );

        return false;
    }
}

}