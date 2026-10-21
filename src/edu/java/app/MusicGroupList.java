package edu.java.app;

import edu.java.app.exceptions.EmptyGroupNameException;
import edu.java.app.exceptions.InvalidYearException;
import edu.java.app.files.TextFileManager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Экранная форма "Music group list".
 * <p>
 * Предоставляет графический интерфейс управления списком музыкальных групп:
 * быстрое сохранение (Ctrl+S), сохранение как, импорт, сортировка, поиск,
 * добавление и удаление записей.
 *
 * @author Оганнисян Э. А.
 * @version 1.5
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

    /** Кнопка вызова справки */
    private JButton moreInformationButton;

    /** Переменные состояния сортировки */
    private int lastSortedColumn = -1;
    private boolean sortAscending = true;

    /**
     * Проверяет корректность данных, введённых пользователем в ячейку таблицы.
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

        // Проверка колонки "year" (индекс 5) — диапазон 1900-2026
        if (column == 5) {
            int year = Integer.parseInt(strVal);
            if (year < 1900 || year > 2026) {
                throw new InvalidYearException("Год основания должен быть в диапазоне от 1900 до 2026 года!");
            }
        }
    }

    /**
     * Пересчитывает и обновляет порядковые номера (колонка №) для всех строк таблицы.
     */
    public void updateRowNumbers() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
    }

    /**
     * Сортирует строки таблицы по выбранной колонке.
     *
     * @param columnIndex индекс колонки для сортировки
     */
    @SuppressWarnings("unchecked")
    private void sortTableByColumn(int columnIndex) {
        if (model.getRowCount() == 0) {
            return;
        }

        if (lastSortedColumn == columnIndex) {
            sortAscending = !sortAscending;
        } else {
            lastSortedColumn = columnIndex;
            sortAscending = true;
        }

        Vector<Vector<Object>> data = (Vector) model.getDataVector();

        data.sort((row1, row2) -> {
            Object val1 = row1.get(columnIndex);
            Object val2 = row2.get(columnIndex);

            if (val1 == null && val2 == null) return 0;
            if (val1 == null) return sortAscending ? -1 : 1;
            if (val2 == null) return sortAscending ? 1 : -1;

            int cmp;
            if (val1 instanceof Number && val2 instanceof Number) {
                cmp = Double.compare(((Number) val1).doubleValue(), ((Number) val2).doubleValue());
            } else {
                cmp = val1.toString().compareToIgnoreCase(val2.toString());
            }

            return sortAscending ? cmp : -cmp;
        });

        updateRowNumbers();
        model.fireTableDataChanged();
    }

    /**
     * Метод построения и визуализации экранной формы.
     */
    public void show() {

        frame = new JFrame("Music group list");
        frame.setSize(950, 550);
        frame.setLocation(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ВЕРХНЯЯ ПАНЕЛЬ ИНСТРУМЕНТОВ
        toolBar = new JToolBar("Панель инструментов");
        toolBar.setFloatable(false);
        toolBar.setPreferredSize(new java.awt.Dimension(950, 60));
        toolBar.setMargin(new java.awt.Insets(5, 5, 5, 5));

        JButton saveButton     = new JButton("Save");
        JButton saveAsButton   = new JButton("Save As...");
        JButton importButton   = new JButton("Import");
        JButton deleteButton   = new JButton("Delete");
        JButton findButton     = new JButton("Find");
        JButton sortButton     = new JButton("Sort ▼");
        JButton addButton      = new JButton("Add");

        java.awt.Dimension btnSize = new java.awt.Dimension(500, 500);
        saveButton.setPreferredSize(btnSize);
        saveAsButton.setPreferredSize(btnSize);
        importButton.setPreferredSize(btnSize);
        deleteButton.setPreferredSize(btnSize);
        findButton.setPreferredSize(btnSize);
        sortButton.setPreferredSize(btnSize);
        addButton.setPreferredSize(btnSize);

        // Подсказки
        saveButton.setToolTipText("Быстрое сохранение файла (Ctrl + S)");
        saveAsButton.setToolTipText("Сохранить в новый текстовый файл...");
        importButton.setToolTipText("Загрузить данные из текстового файла");
        deleteButton.setToolTipText("Удалить выбранную группу");
        findButton.setToolTipText("Поиск группы по названию");
        sortButton.setToolTipText("Сортировка по выбранной колонке");
        addButton.setToolTipText("Добавить новую группу");

        toolBar.add(saveButton);
        toolBar.add(saveAsButton);
        toolBar.add(importButton);
        toolBar.add(deleteButton);
        toolBar.add(findButton);
        toolBar.add(sortButton);
        toolBar.add(addButton);

        frame.add(toolBar, BorderLayout.NORTH);

        // ШАПКА ТАБЛИЦЫ
        String[] columns = {
                "№", "icon", "name", "rate",
                "hit", "year", "leader", "more information"
        };

        // ТАБЛИЦА (изначально пустая)
        model = new DefaultTableModel(new Object[0][columns.length], columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 7;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                try {
                    validateCellValue(column, aValue);

                    if (column == 0 || column == 3 || column == 5) {
                        super.setValueAt(Integer.parseInt(aValue.toString().trim()), row, column);
                    } else {
                        super.setValueAt(aValue.toString().trim(), row, column);
                    }

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "В данную ячейку необходимо ввести целое число!",
                            "Ошибка типа данных",
                            JOptionPane.ERROR_MESSAGE
                    );
                } catch (InvalidYearException | EmptyGroupNameException e) {
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

        // ВЫПАДАЮЩЕЕ МЕНЮ СОРТИРОВКИ
        JPopupMenu sortPopupMenu = new JPopupMenu();
        for (int i = 2; i <= 6; i++) {
            final int colIndex = i;
            JMenuItem item = new JMenuItem(columns[i]);
            item.addActionListener(e -> sortTableByColumn(colIndex));
            sortPopupMenu.add(item);
        }
        sortButton.addActionListener(e -> sortPopupMenu.show(sortButton, 0, sortButton.getHeight()));

        // НИЖНЯЯ ПАНЕЛЬ
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        moreInformationButton = new JButton("?");
        moreInformationButton.setToolTipText("Информация о программе");
        bottomPanel.add(moreInformationButton);

        // ОБРАБОТЧИКИ СОХРАНЕНИЯ И ИМПОРТА
        saveButton.addActionListener(event -> TextFileManager.save(frame, model));
        saveAsButton.addActionListener(event -> TextFileManager.saveAs(frame, model));
        importButton.addActionListener(event -> TextFileManager.importData(frame, model));

        // ГОРЯЧАЯ КЛАВИША CTRL + S
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlS, "saveShortcut");
        frame.getRootPane().getActionMap().put("saveShortcut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextFileManager.save(frame, model);
            }
        });

        // ПОИСК
        findButton.addActionListener(event -> {
            String query = JOptionPane.showInputDialog(
                    frame,
                    "Введите название группы:",
                    "Поиск",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (query == null || query.trim().isEmpty()) {
                return;
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                Object nameVal = model.getValueAt(i, 2);
                if (nameVal != null && nameVal.toString().toLowerCase().contains(query.trim().toLowerCase())) {
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
        });

        // УДАЛЕНИЕ СТРОКИ
        deleteButton.addActionListener(event -> {
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
                updateRowNumbers();
            }
        });

        // ДОБАВЛЕНИЕ СТРОКИ
        addButton.addActionListener(event -> {
            int newNumber = model.getRowCount() + 1;
            model.addRow(new Object[]{
                    newNumber, "", "Новая группа", 0, "", 2024, "", "Подробнее"
            });
            updateRowNumbers();
        });

        // КНОПКА ПОМОЩИ
        moreInformationButton.addActionListener(event -> JOptionPane.showMessageDialog(
                frame,
                "Программа учёта музыкальных групп.\nГорячие клавиши:\n[Ctrl + S] — быстрое сохранение",
                "О программе",
                JOptionPane.INFORMATION_MESSAGE
        ));

        // КЛИК ПО ЯЧЕЙКЕ "more information" (индекс 7)
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();

                if (col == 7 && row >= 0) {
                    Object name   = model.getValueAt(row, 2);
                    Object rate   = model.getValueAt(row, 3);
                    Object hit    = model.getValueAt(row, 4);
                    Object year   = model.getValueAt(row, 5);
                    Object leader = model.getValueAt(row, 6);

                    JOptionPane.showMessageDialog(
                            frame,
                            "Информация о группе:\n\n" +
                            "🎸 Название: " + name + "\n" +
                            "🎤 Лидер / Вокал: " + leader + "\n" +
                            "📅 Год основания: " + year + "\n" +
                            "🔥 Главный хит: " + hit + "\n" +
                            "⭐ Рейтинг: " + rate + " / 5",
                            "Карточка: " + name,
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}