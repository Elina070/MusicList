package edu.java.app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Экранная форма "Music group list".
 * <p>
 * Отображает список музыкальных групп и предоставляет пользователю
 * доступ к основным операциям управления: сохранение, импорт,
 * удаление, редактирование, поиск, сортировка, добавление.
 * <p>
 * Класс содержит описание графических компонентов формы
 * и метод {@link #show()} для её построения и визуализации.
 *
 * @author Оганнисян Э. А.
 * @version 1.1
 */
public class MusicGroupList {

    // ==================== Графические компоненты ====================

    /** Главное окно приложения */
    private JFrame frame;

    /** Панель инструментов с кнопками управления */
    private JToolBar toolBar;

    /** Таблица для отображения списка групп */
    private JTable table;

    /** Модель данных таблицы */
    private DefaultTableModel model;

    /** Панель прокрутки для таблицы */
    private JScrollPane scroll;

    /** Кнопка "Назад" */
    private JButton backButton;

    // ==================== Методы ====================

    /**
     * Метод построения и визуализации экранной формы.
     * <p>
     * Создаёт окно, размещает все графические компоненты
     * с помощью компоновщика {@link BorderLayout}
     * и делает окно видимым.
     */
    public void show() {

        // --- Создание главного окна ---
        frame = new JFrame("Music group list");
        frame.setSize(900, 550);
        frame.setLocation(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // --- Создание панели инструментов ---
        toolBar = new JToolBar("Панель инструментов");
        toolBar.setFloatable(false);

        // Создание кнопок
        JButton saveButton   = new JButton("Save");
        JButton importButton = new JButton("Import");
        JButton deleteButton = new JButton("Delete");
        JButton editButton   = new JButton("Edit");
        JButton findButton   = new JButton("Find");
        JButton sortButton   = new JButton("Sort");
        JButton addButton    = new JButton("Add");

        // Подсказки при наведении курсора
        saveButton.setToolTipText("Сохранить / Сохранить как");
        importButton.setToolTipText("Импорт из файла или буфера обмена");
        deleteButton.setToolTipText("Удалить таблицу / по имени / по номеру строки");
        editButton.setToolTipText("Редактировать выбранную группу");
        findButton.setToolTipText("Поиск по критерию");
        sortButton.setToolTipText("Сортировка по критерию (возрастание/убывание)");
        addButton.setToolTipText("Добавить новую группу");

        // Добавление кнопок на панель инструментов
        toolBar.add(saveButton);
        toolBar.add(importButton);
        toolBar.add(deleteButton);
        toolBar.add(editButton);
        toolBar.add(findButton);
        toolBar.add(sortButton);
        toolBar.add(addButton);

        // Размещение панели инструментов в верхней части окна
        frame.add(toolBar, BorderLayout.NORTH);

        // --- Создание таблицы с данными ---
        String[] columns = {
                "№", "icon", "name", "rate",
                "hit", "year", "leader", "more information"
        };

        Object[][] data = { //ТУТ ПОТОМ НОРМАЛЬНО ЗАПОЛНИТЬ С ФАЙЛА ИЛИ БУФЕРА
                {1, "", "The Beatles", 5, "Hey Jude",
                        1960, "John Lennon", "Легендарная группа"},
                {2, "", "Queen", 5, "Bohemian Rhapsody",
                        1970, "Freddie Mercury", "Рок-классика"},
                {3, "", "Nirvana", 4, "Smells Like Teen Spirit",
                        1987, "Kurt Cobain", "Гранж"}
        };

        model = new DefaultTableModel(data, columns);
        table = new JTable(model);
        scroll = new JScrollPane(table);

        // Размещение таблицы в центральной части окна
        frame.add(scroll, BorderLayout.CENTER);

        // --- Создание нижней панели ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("← Назад");
        backButton.setToolTipText("Вернуться в главное меню");
        bottomPanel.add(backButton);

        // Размещение нижней панели в нижней части окна
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // --- Визуализация формы ---
        frame.setVisible(true);
    }
}