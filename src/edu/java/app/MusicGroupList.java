package edu.java.app;

import edu.java.app.exceptions.EmptyGroupNameException;
import edu.java.app.exceptions.InvalidYearException;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Экранная форма "Music group list".
 * <p>
 * Отображает список музыкальных групп и предоставляет пользователю
 * доступ к основным операциям управления: сохранение, импорт,
 * удаление, редактирование, поиск, сортировка, добавление.
 * В версии 1.3 добавлена валидация данных и обработка исключений.
 *
 * @author Оганнисян Э. А.
 * @version 1.3
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

    /** Кнопка "дополнительной информации" */
    private JButton moreInformationButton;

    /**
     * Проверяет корректность данных, введённых пользователем в ячейку таблицы.
     * <p>
     * Выполняет контроль текстовых и числовых полей: проверяет наименование группы
     * на пустоту, а также проверяет соответствие года основания и рейтинга числовому типу.
     *
     * @param column индекс редактируемой колонки таблицы
     * @param value  введённое значение ячейки
     * @throws InvalidYearException    если год основания выходит за границы 1900–2026 гг.
     * @throws EmptyGroupNameException если название группы не содержит символов
     * @throws NumberFormatException   если вместо целого числа введён текст
     */
    
    
    
    private void validateCellValue(int column, Object value)
            throws InvalidYearException, EmptyGroupNameException, NumberFormatException {

        if (value == null) {
            throw new EmptyGroupNameException("Значение не может быть null!");
        }

        String strVal = value.toString().trim();

        // Проверка колонки "name" (индекс 2)
        if (column == 2 && strVal.isEmpty()) {
            throw new EmptyGroupNameException("Название группы не может быть пустым!");
        }

        // Проверка колонки "rate" (индекс 3) — только на целое число
        if (column == 3) {
            Integer.parseInt(strVal);
        }

        // Проверка колонки "year" (индекс 5) — на целое число и диапазон
        if (column == 5) {
            int year = Integer.parseInt(strVal);
            if (year < 1900 || year > 2026) {
                throw new InvalidYearException("Год основания должен быть в диапазоне от 1900 до 2026 года!");
            }
        }
    }

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
        toolBar.setPreferredSize(new java.awt.Dimension(900, 60));
        toolBar.setMargin(new java.awt.Insets(5, 5, 5, 5));

        JButton saveButton   = new JButton("Save");
        JButton importButton = new JButton("Import");
        JButton deleteButton = new JButton("Delete");
        JButton findButton   = new JButton("Find");
        JButton sortButton   = new JButton("Sort");
        JButton addButton    = new JButton("Add");

        java.awt.Dimension btnSize = new java.awt.Dimension(500, 500);
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

        Object[][] data = {
                {1, "", "The Beatles", 5, "Hey Jude", 1960, "John Lennon", "Легендарная группа"},
                {2, "", "Queen", 5, "Bohemian Rhapsody", 1970, "Freddie Mercury", "Рок-классика"},
                {3, "", "Nirvana", 4, "Smells Like Teen Spirit", 1987, "Kurt Cobain", "Гранж"}
        };

        model = new DefaultTableModel(data, columns) {
            /**
             * Определяет, можно ли редактировать ячейку таблицы.
             * Колонка № (индекс 0) и "more information" (индекс 7) — только для чтения.
             *
             * @param row    номер строки
             * @param column номер колонки
             * @return true, если ячейку можно редактировать
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 7;
            }

            /**
             * Устанавливает значение в ячейку таблицы с предварительной валидацией.
             * <p>
             * Перехватывает пользовательский ввод, вызывает метод контроля ошибок
             * и сохраняет числовые данные в типе Integer для корректной работы сортировки.
             *
             * @param aValue новое значение ячейки
             * @param row    индекс редактируемой строки
             * @param column индекс редактируемой колонки
             */
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                try {
                    // Контроль введенного значения с помощью метода с валидацией
                    validateCellValue(column, aValue);

                    // Сохранение числовых полей как Integer для будущей правильной сортировки
                    if (column == 3 || column == 5) {
                        super.setValueAt(Integer.parseInt(aValue.toString().trim()), row, column);
                    } else {
                        super.setValueAt(aValue.toString().trim(), row, column);
                    }

                } catch (NumberFormatException e) { // 1!!!!!!!!
                    // Перехват стандартного исключения формата чисел
                    JOptionPane.showMessageDialog(
                            frame,
                            "В данную ячейку необходимо ввести целое число!",
                            "Ошибка типа данных",
                            JOptionPane.ERROR_MESSAGE
                    );
                } catch (InvalidYearException | EmptyGroupNameException e) {//2!!!!!
                    // Перехват собственных пользовательских исключений
                    JOptionPane.showMessageDialog(
                            frame,
                            e.getMessage(),
                            "Ошибка валидации",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        };

        table = new JTable(model);
        scroll = new JScrollPane(table);

        frame.add(scroll, BorderLayout.CENTER);

        // НИЖНЯЯ ПАНЕЛЬ
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        moreInformationButton = new JButton("?");
        moreInformationButton.setToolTipText("Информация об использовании");
        bottomPanel.add(moreInformationButton);

        // ЗАГЛУШКИ
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

        // РАБОЧИЕ ФУНКЦИИ

        findButton.addActionListener(new ActionListener() {
            /**
             * Обрабатывает нажатие кнопки Find.
             * Запрашивает у пользователя название группы
             * и выделяет первую подходящую строку в таблице.
             *
             * @param event событие нажатия кнопки
             */
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

                for (int i = 0; i < model.getRowCount(); i++) {
                    String name = (String) model.getValueAt(i, 2);
                    if (name.toLowerCase().contains(query.toLowerCase())) {
                        table.setRowSelectionInterval(i, i);
                        table.scrollRectToVisible(table.getCellRect(i, 0, true));
                        return;
                    }
                }

                JOptionPane.showMessageDialog(
                        frame,
                        "Группа \"" + query + "\" не найдена",
                        "Результат поиска",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            /**
             * Обрабатывает нажатие кнопки Delete.
             * Удаляет выделенную строку из таблицы.
             * Если строка не выбрана — показывает предупреждение.
             *
             * @param event событие нажатия кнопки
             */
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
                    model.removeRow(row);
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            /**
             * Обрабатывает нажатие кнопки Add.
             * Добавляет в таблицу новую строку с очередным номером.
             *
             * @param event событие нажатия кнопки
             */
            public void actionPerformed(ActionEvent event) {
                int newNumber = model.getRowCount() + 1;
                model.addRow(new Object[]{
                        newNumber, "", "Новая группа", 0, "", 2024, "", ""
                });
            }
        });

        moreInformationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(frame,
                        "*Инфа о работе приложения*",
                        "О программе",
                        JOptionPane.QUESTION_MESSAGE);
            }
        });

        table.addMouseListener(new java.awt.event.MouseListener() {
            /**
             * Обрабатывает двойной клик по таблице.
             * При двойном клике по колонке "more information"
             * выводит сообщение о переходе на страницу с информацией о группе.
             *
             * @param event событие клика мышью
             */
            public void mouseClicked(java.awt.event.MouseEvent event) {
                if (event.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    if (col == 7 && row >= 0) {
                        String name = (String) model.getValueAt(row, 2);
                        JOptionPane.showMessageDialog(frame,
                                "Переход на страницу с большей информацией о группе \"" + name + "\"");
                    }
                }
            }
            public void mousePressed(java.awt.event.MouseEvent e)  {}
            public void mouseReleased(java.awt.event.MouseEvent e) {}
            public void mouseEntered(java.awt.event.MouseEvent e)  {}
            public void mouseExited(java.awt.event.MouseEvent e)   {}
        });

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}