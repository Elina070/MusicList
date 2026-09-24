package edu.java.app;

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

    /** Кнопка "дополнительной информации" */
    private JButton moreInformationButton;


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

        model = new DefaultTableModel(data, columns) { 
        	//ПОСЛЕДНЮЮ ЯЧЕЙКУ НЕЛЬЗЯ РЕДАЧИТЬ
        	//ОНА КАК КНОПКА 
            /**
             * Определяет, можно ли редактировать ячейку таблицы.
             * Колонка "more information" (индекс 7) — только для чтения,
             * чтобы двойной клик по ней открывал окно информации,
             * а не начинал редактирование.
             *
             * @param row    номер строки
             * @param column номер колонки
             * @return true, если ячейку можно редактировать
             */
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 7;
            }
        };
        table = new JTable(model);
        scroll = new JScrollPane(table);

        // Размещение таблицы в центральной части окна
        frame.add(scroll, BorderLayout.CENTER);
        
        
        
        
		// НИЖНЯЯ ПАНЕЛЬ
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        moreInformationButton = new JButton("?");
        moreInformationButton.setToolTipText("Информация об использовании");
        bottomPanel.add(moreInformationButton);
        
        
        
        
        
        
        
        //нажатие на кнопки ********ЭТО ЗАГЛУШКА********
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
        //*********************************************************
        
        // ЭТО РАБОЧИЕ ФУНКЦИИ НАЖАТИЯ
        
        findButton.addActionListener(new ActionListener() {
            /**
             * Обрабатывает нажатие кнопки Find.
             * Запрашивает у пользователя название группы
             * и выделяет первую подходящую строку в таблице.
             *
             * @param event событие нажатия кнопки
             */
            public void actionPerformed(ActionEvent event) {
                // 1. Спросить у пользователя, что искать
                String query = JOptionPane.showInputDialog(
                    frame,
                    "Введите название группы:",
                    "Поиск",
                    JOptionPane.QUESTION_MESSAGE
                );

                // 2. Если пользователь нажал "Отмена" или ничего не ввёл — выйти
                if (query == null || query.isEmpty()) {
                    return;
                }

                // 3. Пробежаться по всем строкам и найти совпадение
                for (int i = 0; i < model.getRowCount(); i++) {
                    String name = (String) model.getValueAt(i, 2);   // колонка "name"
                    if (name.toLowerCase().contains(query.toLowerCase())) {
                        // 4. Выделить найденную строку
                        table.setRowSelectionInterval(i, i);
                        table.scrollRectToVisible(table.getCellRect(i, 0, true));
                        return;   // нашли — выходим
                    }
                }

                // 5. Ничего не нашли
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
                    newNumber, "", "Новая группа", 0, "", 0, "", ""
                });
            }
        });
        
        moreInformationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(frame,
                        "*Инфа о работе приложения*",
                        "О программе",
                        JOptionPane.QUESTION_MESSAGE);
                // showMessageDialog метод с возможностью перегрузки
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