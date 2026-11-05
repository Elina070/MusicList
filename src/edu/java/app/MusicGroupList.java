
package edu.java.app;

import edu.java.app.exceptions.EmptyGroupNameException;
import edu.java.app.exceptions.InvalidYearException;
import edu.java.app.files.TextFileManager;
import edu.java.app.files.XmlFileManager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Экранная форма "Music group list".
 *
 * @author Оганнисян Э. А.
 * @version 1.6
 */
public class MusicGroupList {

    private JFrame frame;
    private JToolBar toolBar;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scroll;
    private JButton moreInformationButton;

    private int lastSortedColumn = -1;
    private boolean sortAscending = true;

    /** Текущий формат файла */
    private String currentFormat;

    /**
     * Проверяет корректность данных в ячейке.
     */
    private void validateCellValue(int column, Object value)
            throws InvalidYearException,
            EmptyGroupNameException,
            NumberFormatException {

        if (value == null) {
            throw new EmptyGroupNameException(
                    "Значение не может быть null!"
            );
        }

        String strVal = value.toString().trim();

        if (column == 2 && strVal.isEmpty()) {
            throw new EmptyGroupNameException(
                    "Название группы не может быть пустым!"
            );
        }

        if (column == 3) {
            Integer.parseInt(strVal);
        }

        if (column == 5) {
            int year = Integer.parseInt(strVal);

            if (year < 1900 || year > 2026) {
                throw new InvalidYearException(
                        "Год основания должен быть в диапазоне от 1900 до 2026 года!"
                );
            }
        }
    }

    /**
     * Обновляет порядковые номера строк.
     */
    public void updateRowNumbers() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
    }

    /**
     * Сортирует таблицу по выбранной колонке.
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

        Vector<Vector<Object>> data =
                (Vector) model.getDataVector();

        data.sort((row1, row2) -> {
            Object val1 = row1.get(columnIndex);
            Object val2 = row2.get(columnIndex);

            if (val1 == null && val2 == null) return 0;
            if (val1 == null) return sortAscending ? -1 : 1;
            if (val2 == null) return sortAscending ? 1 : -1;

            int cmp;

            if (val1 instanceof Number && val2 instanceof Number) {
                cmp = Double.compare(
                        ((Number) val1).doubleValue(),
                        ((Number) val2).doubleValue()
                );
            } else {
                cmp = val1.toString().compareToIgnoreCase(
                        val2.toString()
                );
            }

            return sortAscending ? cmp : -cmp;
        });

        updateRowNumbers();
        model.fireTableDataChanged();
    }

    /**
     * Сохраняет данные в текущем формате.
     */
    private void save() {
        if ("xml".equals(currentFormat)) {
            XmlFileManager.save(frame, model);
        } else if ("txt".equals(currentFormat)) {
            TextFileManager.save(frame, model);
        } else {
            saveAs();
        }
    }

    /**
     * Сохраняет данные с выбором формата.
     */
    private void saveAs() {
        String[] formats = {"TXT", "XML"};

        int choice = JOptionPane.showOptionDialog(
                frame,
                "Выберите формат файла:",
                "Сохранить как",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                formats,
                formats[0]
        );

        if (choice == 0) {
            File file = chooseSaveFile("txt");

            if (file != null
                    && TextFileManager.saveToFile(frame, model, file)) {
                currentFormat = "txt";
            }

        } else if (choice == 1) {
            File file = chooseSaveFile("xml");

            if (file != null
                    && XmlFileManager.saveToFile(frame, model, file)) {
                currentFormat = "xml";
            }
        }
    }

    /**
     * Выбирает файл для сохранения.
     */
    private File chooseSaveFile(String format) {
        JFileChooser chooser = new JFileChooser();

        String extension = "." + format;

        chooser.setDialogTitle("Сохранить файл");
        chooser.setSelectedFile(
                new File("music_groups" + extension)
        );

        chooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        format.toUpperCase() + " files (*" + extension + ")",
                        format
                )
        );

        if (chooser.showSaveDialog(frame)
                != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(extension)) {
            file = new File(file.getAbsolutePath() + extension);
        }

        if (file.exists()) {
            int answer = JOptionPane.showConfirmDialog(
                    frame,
                    "Файл уже существует. Перезаписать его?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION
            );

            if (answer != JOptionPane.YES_OPTION) {
                return null;
            }
        }

        return file;
    }

    /**
     * Импортирует TXT или XML через один диалог выбора файла.
     */
    private void importData() {
        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Импорт данных");

        chooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Поддерживаемые файлы (*.txt, *.xml)",
                        "txt", "xml"
                )
        );

        if (chooser.showOpenDialog(frame)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".txt")) {
            if (TextFileManager.importData(frame, model, file)) {
                currentFormat = "txt";
            }

        } else if (fileName.endsWith(".xml")) {
            if (XmlFileManager.importData(frame, model, file)) {
                currentFormat = "xml";
            }

        } else {
            JOptionPane.showMessageDialog(
                    frame,
                    "Выбран неподдерживаемый формат файла!",
                    "Ошибка импорта",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Создаёт меню File.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem saveAsItem = new JMenuItem("Save As...");
        JMenuItem importItem = new JMenuItem("Import");

        saveItem.addActionListener(e -> save());
        saveAsItem.addActionListener(e -> saveAs());
        importItem.addActionListener(e -> importData());

        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(importItem);

        menuBar.add(fileMenu);

        return menuBar;
    }

    /**
     * Построение экранной формы.
     */
    public void show() {

        frame = new JFrame("Music group list");
        frame.setSize(950, 550);
        frame.setLocation(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.setJMenuBar(createMenuBar());

        // ПАНЕЛЬ ИНСТРУМЕНТОВ
        toolBar = new JToolBar("Панель инструментов");
        toolBar.setFloatable(false);
        toolBar.setPreferredSize(new java.awt.Dimension(950, 60));
        toolBar.setMargin(new java.awt.Insets(5, 5, 5, 5));

        JButton deleteButton = new JButton("Delete");
        JButton findButton = new JButton("Find");
        JButton sortButton = new JButton("Sort ▼");
        JButton addButton = new JButton("Add");

        java.awt.Dimension btnSize =
                new java.awt.Dimension(500, 500);

        deleteButton.setPreferredSize(btnSize);
        findButton.setPreferredSize(btnSize);
        sortButton.setPreferredSize(btnSize);
        addButton.setPreferredSize(btnSize);

        deleteButton.setToolTipText("Удалить выбранную группу");
        findButton.setToolTipText("Поиск группы по названию");
        sortButton.setToolTipText("Сортировка по выбранной колонке");
        addButton.setToolTipText("Добавить новую группу");

        toolBar.add(deleteButton);
        toolBar.add(findButton);
        toolBar.add(sortButton);
        toolBar.add(addButton);

        frame.add(toolBar, BorderLayout.NORTH);

        // СТОЛБЦЫ ТАБЛИЦЫ
        String[] columns = {
                "№", "icon", "name", "rate",
                "hit", "year", "leader", "more information"
        };

        // МОДЕЛЬ ТАБЛИЦЫ
        model = new DefaultTableModel(
                new Object[0][columns.length],
                columns
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 7;
            }

            @Override
            public void setValueAt(
                    Object aValue,
                    int row,
                    int column
            ) {
                try {
                    validateCellValue(column, aValue);

                    if (column == 0 || column == 3 || column == 5) {
                        super.setValueAt(
                                Integer.parseInt(aValue.toString().trim()),
                                row,
                                column
                        );
                    } else {
                        super.setValueAt(
                                aValue.toString().trim(),
                                row,
                                column
                        );
                    }

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "В данную ячейку необходимо ввести целое число!",
                            "Ошибка типа данных",
                            JOptionPane.ERROR_MESSAGE
                    );

                } catch (
                        InvalidYearException
                        | EmptyGroupNameException e
                ) {
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

        // МЕНЮ СОРТИРОВКИ
        JPopupMenu sortPopupMenu = new JPopupMenu();

        for (int i = 2; i <= 6; i++) {
            final int colIndex = i;

            JMenuItem item = new JMenuItem(columns[i]);

            item.addActionListener(
                    e -> sortTableByColumn(colIndex)
            );

            sortPopupMenu.add(item);
        }

        sortButton.addActionListener(
                e -> sortPopupMenu.show(
                        sortButton, 0, sortButton.getHeight()
                )
        );

        // НИЖНЯЯ ПАНЕЛЬ
        JPanel bottomPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        moreInformationButton = new JButton("?");
        moreInformationButton.setToolTipText(
                "Информация о программе"
        );

        bottomPanel.add(moreInformationButton);

        // CTRL + S
        KeyStroke ctrlS = KeyStroke.getKeyStroke(
                KeyEvent.VK_S,
                KeyEvent.CTRL_DOWN_MASK
        );

        frame.getRootPane()
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(ctrlS, "saveShortcut");

        frame.getRootPane()
                .getActionMap()
                .put("saveShortcut", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        save();
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

                if (nameVal != null
                        && nameVal.toString().toLowerCase()
                        .contains(query.trim().toLowerCase())) {

                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(
                            table.getCellRect(i, 0, true)
                    );

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

        // УДАЛЕНИЕ
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

        // ДОБАВЛЕНИЕ
        addButton.addActionListener(event -> {
            int newNumber = model.getRowCount() + 1;

            model.addRow(new Object[]{
                    newNumber, "", "Новая группа",
                    0, "", 2024, "", "Подробнее"
            });

            updateRowNumbers();
        });

        // СПРАВКА
        moreInformationButton.addActionListener(event ->
                JOptionPane.showMessageDialog(
                        frame,
                        "Программа учёта музыкальных групп.\n"
                                + "Горячие клавиши:\n"
                                + "[Ctrl + S] — быстрое сохранение",
                        "О программе",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        // КЛИК ПО ЯЧЕЙКЕ MORE INFORMATION
        table.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent event
                    ) {
                        int row = table.getSelectedRow();
                        int col = table.getSelectedColumn();

                        if (col == 7 && row >= 0) {
                            Object name = model.getValueAt(row, 2);
                            Object rate = model.getValueAt(row, 3);
                            Object hit = model.getValueAt(row, 4);
                            Object year = model.getValueAt(row, 5);
                            Object leader = model.getValueAt(row, 6);

                            JOptionPane.showMessageDialog(
                                    frame,
                                    "Информация о группе:\n\n"
                                            + "🎸 Название: " + name + "\n"
                                            + "🎤 Лидер / Вокал: " + leader + "\n"
                                            + "📅 Год основания: " + year + "\n"
                                            + "🔥 Главный хит: " + hit + "\n"
                                            + "⭐ Рейтинг: " + rate + " / 5",
                                    "Карточка: " + name,
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        }
                    }
                }
        );

        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
