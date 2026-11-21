
package edu.java.app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Экранная форма "Music group list".
 * Отображает список музыкальных групп и предоставляет
 * пользователю основные операции управления.
 *
 * @author Оганнисян Э. А.
 * @version 1.3
 */
public class MusicGroupList {

    /** Главное окно приложения */
    private JFrame frame;

    /** Панель инструментов */
    private JToolBar toolBar;

    /** Таблица музыкальных групп */
    private JTable table;

    /** Модель данных таблицы */
    private DefaultTableModel model;

    /** Панель прокрутки */
    private JScrollPane scroll;

    /** Кнопка дополнительной информации */
    private JButton moreInformationButton;

    /**
     * Конструктор. Создаёт и заполняет модель таблицы.
     */
    public MusicGroupList() {
        String[] columns = {
            "№", "icon", "name", "rate",
            "hit", "year", "leader", "more information"
        };

        Object[][] data = {
            {1, "", "The Beatles", 5, "Hey Jude",
                1960, "John Lennon", "Легендарная группа"},
            {2, "", "Queen", 5, "Bohemian Rhapsody",
                1970, "Freddie Mercury", "Рок-классика"},
            {3, "", "Nirvana", 4, "Smells Like Teen Spirit",
                1987, "Kurt Cobain", "Гранж"}
        };

        model = new DefaultTableModel(data, columns) {
            /**
             * Определяет возможность редактирования ячейки.
             *
             * @param row номер строки
             * @param column номер столбца
             * @return false для последнего столбца
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 7;
            }
        };
    }

    /**
     * Добавляет новую музыкальную группу в таблицу.
     */
    public void addGroup() {
        int newNumber = model.getRowCount() + 1;

        model.addRow(new Object[]{
            newNumber, "", "Новая группа", 0, "", 0, "", ""
        });
    }

    /**
     * Удаляет музыкальную группу по номеру строки.
     *
     * @param row номер удаляемой строки
     * @throws IllegalArgumentException если номер строки некорректен
     */
    public void deleteGroup(int row) {
        if (row < 0 || row >= model.getRowCount()) {
            throw new IllegalArgumentException(
                "Некорректный номер строки"
            );
        }

        model.removeRow(row);
    }

    /**
     * Ищет музыкальную группу по названию.
     * Поиск не зависит от регистра и допускает частичное совпадение.
     *
     * @param query поисковый запрос
     * @return номер найденной строки или -1, если совпадений нет
     */
    public int findGroup(String query) {
        if (query == null || query.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            String name = (String) model.getValueAt(i, 2);

            if (name.toLowerCase().contains(query.toLowerCase())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Возвращает количество музыкальных групп.
     *
     * @return количество строк таблицы
     */
    public int getGroupCount() {
        return model.getRowCount();
    }

    /**
     * Возвращает название группы по номеру строки.
     *
     * @param row номер строки
     * @return название музыкальной группы
     */
    public String getGroupName(int row) {
        return (String) model.getValueAt(row, 2);
    }

    /**
     * Проверяет, доступно ли редактирование ячейки.
     *
     * @param row номер строки
     * @param column номер столбца
     * @return возможность редактирования
     */
    public boolean isCellEditable(int row, int column) {
        return model.isCellEditable(row, column);
    }

    /**
     * Метод построения и визуализации экранной формы.
     */
    public void show() {

        frame = new JFrame("Music group list");
        frame.setSize(900, 550);
        frame.setLocation(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Верхняя панель
        toolBar = new JToolBar("Панель инструментов");
        toolBar.setFloatable(false);
        toolBar.setPreferredSize(new Dimension(900, 60));
        toolBar.setMargin(new Insets(5, 5, 5, 5));

        JButton saveButton = new JButton("Save");
        JButton importButton = new JButton("Import");
        JButton deleteButton = new JButton("Delete");
        JButton findButton = new JButton("Find");
        JButton sortButton = new JButton("Sort");
        JButton addButton = new JButton("Add");

        Dimension btnSize = new Dimension(500, 500);

        saveButton.setPreferredSize(btnSize);
        importButton.setPreferredSize(btnSize);
        deleteButton.setPreferredSize(btnSize);
        findButton.setPreferredSize(btnSize);
        sortButton.setPreferredSize(btnSize);
        addButton.setPreferredSize(btnSize);

        // Подсказки
        saveButton.setToolTipText("Сохранить / Сохранить как");
        importButton.setToolTipText("Импорт из файла или буфера обмена");
        deleteButton.setToolTipText("Удалить выбранную строку");
        findButton.setToolTipText("Поиск по названию");
        sortButton.setToolTipText("Сортировка по критерию");
        addButton.setToolTipText("Добавить новую группу");

        toolBar.add(saveButton);
        toolBar.add(importButton);
        toolBar.add(deleteButton);
        toolBar.add(findButton);
        toolBar.add(sortButton);
        toolBar.add(addButton);

        frame.add(toolBar, BorderLayout.NORTH);

        // Таблица
        table = new JTable(model);
        scroll = new JScrollPane(table);

        frame.add(scroll, BorderLayout.CENTER);

        // Нижняя панель
        JPanel bottomPanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );

        moreInformationButton = new JButton("?");
        moreInformationButton.setToolTipText(
            "Информация об использовании"
        );

        bottomPanel.add(moreInformationButton);

        // Заглушки
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(frame, "Save нажата");
            }
        });

        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(frame, "Import нажата");
            }
        });

        sortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(frame, "Sort нажата");
            }
        });

        // Поиск
        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                String query = JOptionPane.showInputDialog(
                    frame,
                    "Введите название группы:",
                    "Поиск",
                    JOptionPane.QUESTION_MESSAGE
                );

                if (query == null || query.isEmpty()) {
                    return;
                }

                int row = findGroup(query);

                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                    table.scrollRectToVisible(
                        table.getCellRect(row, 0, true)
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Группа \"" + query + "\" не найдена",
                        "Результат поиска",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        // Удаление
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                int row = table.getSelectedRow();

                if (row == -1) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Не выбрана строка для удаления",
                        "Предупреждение",
                        JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    deleteGroup(row);
                }
            }
        });

        // Добавление
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addGroup();
            }
        });

        // Дополнительная информация
        moreInformationButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "*Инфа о работе приложения*",
                        "О программе",
                        JOptionPane.QUESTION_MESSAGE
                    );
                }
            }
        );

        // Двойной клик по последнему столбцу
        table.addMouseListener(new java.awt.event.MouseListener() {

            public void mouseClicked(java.awt.event.MouseEvent event) {

                if (event.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();

                    if (col == 7 && row >= 0) {
                        String name = getGroupName(row);

                        JOptionPane.showMessageDialog(
                            frame,
                            "Переход на страницу с большей информацией о группе \""
                                + name + "\""
                        );
                    }
                }
            }

            public void mousePressed(java.awt.event.MouseEvent e) {}
            public void mouseReleased(java.awt.event.MouseEvent e) {}
            public void mouseEntered(java.awt.event.MouseEvent e) {}
            public void mouseExited(java.awt.event.MouseEvent e) {}
        });

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
