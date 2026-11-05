
package edu.java.app.files;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для сохранения и загрузки списка музыкальных групп
 * в формате XML.
 *
 * @author Оганнисян Э. А.
 * @version 1.0
 */
public class XmlFileManager {

    private static String currentFilePath;

    /**
     * Сохраняет данные в последний использованный XML-файл.
     */
    public static void save(JFrame frame, DefaultTableModel model) {
        if (currentFilePath == null) {
            saveAs(frame, model);
            return;
        }

        saveToFile(frame, model, new File(currentFilePath));
    }

    /**
     * Сохраняет таблицу в указанный XML-файл.
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

        try {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement("musicGroups");
            document.appendChild(root);

            for (int i = 0; i < model.getRowCount(); i++) {
                Element group = document.createElement("group");
                root.appendChild(group);

                group.setAttribute("icon", getValue(model, i, 1));
                group.setAttribute("name", getValue(model, i, 2));
                group.setAttribute("rate", getValue(model, i, 3));
                group.setAttribute("hit", getValue(model, i, 4));
                group.setAttribute("year", getValue(model, i, 5));
                group.setAttribute("leader", getValue(model, i, 6));
            }

            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();

            Transformer transformer =
                    transformerFactory.newTransformer();

            transformer.setOutputProperty(
                    OutputKeys.INDENT, "yes"
            );

            transformer.setOutputProperty(
                    OutputKeys.ENCODING, "UTF-8"
            );

            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4"
            );

            transformer.transform(
                    new DOMSource(document),
                    new StreamResult(file)
            );

            currentFilePath = file.getAbsolutePath();
            frame.setTitle("Music group list - " + file.getName());

            JOptionPane.showMessageDialog(
                    frame,
                    "Данные успешно сохранены в XML-файл!",
                    "Сохранение",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return true;

        } catch (ParserConfigurationException | TransformerException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Ошибка сохранения XML-файла:\n" + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }

    /**
     * Открывает диалог сохранения XML-файла.
     */
    public static void saveAs(JFrame frame, DefaultTableModel model) {
        javax.swing.JFileChooser chooser =
                new javax.swing.JFileChooser();

        chooser.setDialogTitle("Сохранить XML-файл");
        chooser.setSelectedFile(new File("music_groups.xml"));

        chooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "XML files (*.xml)", "xml"
                )
        );

        if (chooser.showSaveDialog(frame)
                != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(".xml")) {
            file = new File(file.getAbsolutePath() + ".xml");
        }

        if (file.exists()) {
            int answer = JOptionPane.showConfirmDialog(
                    frame,
                    "Файл уже существует. Перезаписать его?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION
            );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }

        saveToFile(frame, model, file);
    }

    /**
     * Загружает данные из XML-файла.
     */
    public static boolean importData(
            JFrame frame,
            DefaultTableModel model,
            File file
    ) {
        try {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            factory.setFeature(
                    "http://apache.org/xml/features/disallow-doctype-decl",
                    true
            );

            factory.setFeature(
                    "http://xml.org/sax/features/external-general-entities",
                    false
            );

            factory.setFeature(
                    "http://xml.org/sax/features/external-parameter-entities",
                    false
            );

            factory.setAttribute(
                    XMLConstants.ACCESS_EXTERNAL_DTD, ""
            );

            factory.setAttribute(
                    XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""
            );

            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();

            if (!root.getTagName().equals("musicGroups")) {
                throw new IllegalArgumentException(
                        "Неверный корневой элемент XML-документа!"
                );
            }

            NodeList groups = root.getElementsByTagName("group");

            List<Object[]> importedRows = new ArrayList<>();

            for (int i = 0; i < groups.getLength(); i++) {
                Element group = (Element) groups.item(i);

                String icon = getAttribute(group, "icon");
                String name = getAttribute(group, "name");
                String rateText = getAttribute(group, "rate");
                String hit = getAttribute(group, "hit");
                String yearText = getAttribute(group, "year");
                String leader = getAttribute(group, "leader");

                if (name.trim().isEmpty()) {
                    throw new IllegalArgumentException(
                            "Название группы не может быть пустым!"
                    );
                }

                int rate = Integer.parseInt(rateText);
                int year = Integer.parseInt(yearText);

                if (year < 1900 || year > 2026) {
                    throw new IllegalArgumentException(
                            "Год основания должен быть в диапазоне 1900–2026!"
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
                    "Данные успешно загружены из XML-файла!",
                    "Импорт",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return true;

        } catch (ParserConfigurationException
                 | SAXException
                 | IOException
                 | IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Ошибка чтения XML-файла:\n" + e.getMessage(),
                    "Ошибка импорта",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }

    private static String getValue(
            DefaultTableModel model,
            int row,
            int column
    ) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private static String getAttribute(
            Element element,
            String attributeName
    ) {
        if (!element.hasAttribute(attributeName)) {
            throw new IllegalArgumentException(
                    "Отсутствует XML-атрибут: " + attributeName
            );
        }

        return element.getAttribute(attributeName);
    }
}
