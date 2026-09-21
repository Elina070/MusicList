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
 * @version 1.2
 */
public class MusicGroupList {

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


    /**
     * Метод построения и визуализации экранной формы.
     * <p>
     * Создаёт окно, размещает все графические компоненты
     * с помощью компоновщика {@link BorderLayout}
     * и делает окно видимым.
     */
    public void show() {

        
        frame = new JFrame("Music group list");
        frame.setSize(900, 550); 
        frame.setLocation(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        				// ВЕРХНЯЯ ПАНЕЛЬ
        toolBar = new JToolBar("Панель инструментов");
        toolBar.setFloatable(false);
        toolBar.setPreferredSize(new java.awt.Dimension(900, 60)); // Размер тулбара
        //кнопки съезжают вниз 
        toolBar.setMargin(new java.awt.Insets(5, 5, 5, 5));

        JButton saveButton   = new JButton("Save");
        JButton importButton = new JButton("Import");
        JButton deleteButton = new JButton("Delete");
        JButton findButton   = new JButton("Find");
        JButton sortButton   = new JButton("Sort");
        JButton addButton    = new JButton("Add");
        //раньше была edit но она бессмыслена пока
        
       
        java.awt.Dimension btnSize = new java.awt.Dimension(500, 500); //Размеры кнопок
        saveButton.setPreferredSize(btnSize);
        importButton.setPreferredSize(btnSize);
        deleteButton.setPreferredSize(btnSize);
        findButton.setPreferredSize(btnSize);
        sortButton.setPreferredSize(btnSize);
        addButton.setPreferredSize(btnSize);

        // Подсказки
        saveButton.setToolTipText("Сохранить / Сохранить как");
        importButton.setToolTipText("Импорт из файла или буфера обмена");
        deleteButton.setToolTipText("Удалить таблицу / по имени / по номеру строки");
        findButton.setToolTipText("Поиск по критерию");
        sortButton.setToolTipText("Сортировка по критерию (возрастание/убывание)");
        addButton.setToolTipText("Добавить новую группу");

        toolBar.add(saveButton);
        toolBar.add(importButton);
        toolBar.add(deleteButton);
        toolBar.add(findButton);
        toolBar.add(sortButton);
        toolBar.add(addButton);

        frame.add(toolBar, BorderLayout.NORTH);

        				// ТАБЛИЦА
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

        		// НИЖНЯЯ ПАНЕЛЬ
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("← Назад");
        backButton.setToolTipText("Вернуться в главное меню");
        bottomPanel.add(backButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}