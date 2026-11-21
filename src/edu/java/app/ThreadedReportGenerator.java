
package edu.java.app;

import edu.java.app.files.XmlFileManager;
import edu.java.app.reports.ReportManager;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Демонстрирует последовательное взаимодействие трёх потоков:
 * загрузка XML в таблицу, подготовка изменённого XML и создание HTML-отчёта.
 *
 * @author Оганнисян Э. А.
 * @version 1.0
 */
public final class ThreadedReportGenerator {

    private static final Object monitor = new Object();
    private static int stage = 0;
    private static Exception failure;

    private ThreadedReportGenerator() {
    }

    /**
     * Запускает три рабочих потока.
     *
     * @param frame окно приложения
     * @param model модель таблицы
     * @param sourceXml исходный XML-файл
     */
    public static void start(
            final JFrame frame,
            final DefaultTableModel model,
            final File sourceXml
    ) {

        synchronized (monitor) {
            stage = 0;
            failure = null;
        }

        final File outputDirectory = new File("reports/output");

        final File editedXml = new File(
                outputDirectory,
                "music_groups_edited.xml"
        );

        final File htmlReport = new File(
                outputDirectory,
                "music_groups.html"
        );

        // ПОТОК 1: ЗАГРУЗКА XML В ТАБЛИЦУ
        Thread loaderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final AtomicBoolean loaded =
                            new AtomicBoolean(false);

                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            loaded.set(
                                    XmlFileManager.importData(
                                            frame,
                                            model,
                                            sourceXml
                                    )
                            );
                        }
                    });

                    if (!loaded.get()) {
                        throw new IOException(
                                "Не удалось загрузить исходный XML-файл."
                        );
                    }

                    completeStage(1);

                } catch (Exception e) {
                    stopWithError(e);
                }
            }
        }, "XML-Loader");

        // ПОТОК 2: ФОРМИРОВАНИЕ ИЗМЕНЁННОГО XML
        Thread editorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    waitForStage(1);

                    final List<Object[]> rows =
                            new ArrayList<Object[]>();

                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {

                            for (int row = 0;
                                 row < model.getRowCount();
                                 row++) {

                                Object[] values = new Object[6];

                                for (int column = 0;
                                     column < 6;
                                     column++) {

                                    Object value = model.getValueAt(
                                            row,
                                            column + 1
                                    );

                                    values[column] = value == null
                                            ? ""
                                            : value.toString().trim();
                                }

                                rows.add(values);
                            }
                        }
                    });

                    writeEditedXml(rows, editedXml);

                    completeStage(2);

                } catch (Exception e) {
                    stopWithError(e);
                }
            }
        }, "XML-Editor");

        // ПОТОК 3: СОЗДАНИЕ HTML-ОТЧЁТА
        Thread reportThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    waitForStage(2);

                    ReportManager.generateHtml(
                            editedXml,
                            htmlReport
                    );

                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(
                                htmlReport.toURI()
                        );
                    }

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(
                                    frame,
                                    "HTML-отчёт создан и открыт в браузере:\n"
                                            + htmlReport.getAbsolutePath(),
                                    "Готово",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        }
                    });

                } catch (Exception e) {
                    stopWithError(e);
                }
            }
        }, "HTML-Report-Builder");

        loaderThread.start();
        editorThread.start();
        reportThread.start();
    }

    /**
     * Сообщает о завершении этапа и пробуждает ожидающие потоки.
     */
    private static void completeStage(int completedStage) {
        synchronized (monitor) {
            stage = completedStage;
            monitor.notifyAll();
        }
    }

    /**
     * Ожидает завершения предыдущего этапа.
     */
    private static void waitForStage(int requiredStage)
            throws Exception {

        synchronized (monitor) {

            while (stage < requiredStage && failure == null) {
                monitor.wait();
            }

            if (failure != null) {
                throw failure;
            }
        }
    }

    /**
     * Останавливает обработку при возникновении ошибки.
     */
    private static void stopWithError(final Exception error) {

        synchronized (monitor) {

            if (failure == null) {
                failure = error;
            }

            monitor.notifyAll();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                JOptionPane.showMessageDialog(
                        null,
                        "Не удалось сформировать отчёт:\n"
                                + error.getMessage(),
                        "Ошибка многопоточной обработки",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    /**
     * Записывает данные таблицы в XML-файл.
     */
    private static void writeEditedXml(
            List<Object[]> rows,
            File outputFile
    ) throws Exception {

        File parent = outputFile.getAbsoluteFile().getParentFile();

        if (parent != null
                && !parent.exists()
                && !parent.mkdirs()) {

            throw new IOException(
                    "Не удалось создать каталог: " + parent
            );
        }

        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();

        Element root = document.createElement("musicGroups");
        document.appendChild(root);

        String[] attributes = {
                "icon",
                "name",
                "rate",
                "hit",
                "year",
                "leader"
        };

        for (Object[] row : rows) {

            Element group = document.createElement("group");
            root.appendChild(group);

            for (int i = 0; i < attributes.length; i++) {

                group.setAttribute(
                        attributes[i],
                        String.valueOf(row[i])
                );
            }
        }

        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();

        transformer.setOutputProperty(
                OutputKeys.INDENT,
                "yes"
        );

        transformer.setOutputProperty(
                OutputKeys.ENCODING,
                "UTF-8"
        );

        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount",
                "4"
        );

        transformer.transform(
                new DOMSource(document),
                new StreamResult(outputFile)
        );
    }
}
