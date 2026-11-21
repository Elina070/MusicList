
package edu.java.app.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

/**
 * Генерирует PDF- и HTML-отчёты по XML-файлу со списком музыкальных групп.
 * Для работы требуется JasperReports 5.0.0 и его зависимости.
 *
 * @author Оганнисян Э. А.
 * @version 1.0
 */
public class ReportManager {

    private static final String PDF_TEMPLATE =
            "reports/templates/music_groups_pdf.jrxml";

    private static final String HTML_TEMPLATE =
            "reports/templates/music_groups_html.jrxml";

    private ReportManager() {
        // Утилитный класс: экземпляр не требуется.
    }

    /**
     * Создаёт PDF-отчёт по XML-файлу.
     *
     * @param xmlFile исходный XML-файл из лабораторной работы № 6
     * @param outputPdf путь к создаваемому PDF-файлу
     * @throws JRException если JasperReports не смог построить отчёт
     * @throws IOException если не удалось создать каталог или файл
     */
    public static void generatePdf(File xmlFile, File outputPdf)
            throws JRException, IOException {

        JasperPrint print = fillReport(PDF_TEMPLATE, xmlFile);

        ensureParentDirectory(outputPdf);

        JasperExportManager.exportReportToPdfFile(
                print,
                outputPdf.getAbsolutePath()
        );
    }

    /**
     * Создаёт HTML-отчёт по XML-файлу.
     *
     * @param xmlFile исходный XML-файл из лабораторной работы № 6
     * @param outputHtml путь к создаваемому HTML-файлу
     * @throws JRException если JasperReports не смог построить отчёт
     * @throws IOException если не удалось создать каталог или файл
     */
    public static void generateHtml(File xmlFile, File outputHtml)
            throws JRException, IOException {

        JasperPrint print = fillReport(HTML_TEMPLATE, xmlFile);

        ensureParentDirectory(outputHtml);

        JasperExportManager.exportReportToHtmlFile(
                print,
                outputHtml.getAbsolutePath()
        );
    }

    /**
     * Загружает XML-данные и заполняет шаблон отчёта.
     *
     * @param templatePath путь к JRXML-шаблону
     * @param xmlFile исходный XML-файл
     * @return заполненный отчёт JasperPrint
     * @throws JRException если произошла ошибка JasperReports
     * @throws IOException если не удалось прочитать XML-файл
     */
    private static JasperPrint fillReport(
            String templatePath,
            File xmlFile
    ) throws JRException, IOException {

        if (xmlFile == null || !xmlFile.isFile()) {
            throw new IllegalArgumentException(
                    "XML-файл не найден: " + xmlFile
            );
        }

        Map<String, Object> parameters =
                new HashMap<String, Object>();

        net.sf.jasperreports.engine.JasperReport report =
                JasperCompileManager.compileReport(templatePath);

        // Читаем XML напрямую через поток.
        try (FileInputStream input =
                     new FileInputStream(xmlFile)) {

            JRXmlDataSource dataSource =
                    new JRXmlDataSource(
                            input,
                            "/musicGroups/group"
                    );

            return JasperFillManager.fillReport(
                    report,
                    parameters,
                    dataSource
            );
        }
    }

    /**
     * Создаёт родительский каталог выходного файла, если его нет.
     *
     * @param outputFile выходной файл
     * @throws IOException если не удалось создать каталог
     */
    private static void ensureParentDirectory(File outputFile)
            throws IOException {

        File parent = outputFile
                .getAbsoluteFile()
                .getParentFile();

        if (parent != null
                && !parent.exists()
                && !parent.mkdirs()) {

            throw new IOException(
                    "Не удалось создать каталог: " + parent
            );
        }
    }

    /**
     * Пример запуска из командной строки.
     *
     * Аргумент 1 — путь к XML-файлу;
     * аргумент 2 — каталог результатов.
     */
    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println(
                    "Использование: ReportManager "
                            + "<music_groups.xml> [output-dir]"
            );

            return;
        }

        File xml = new File(args[0]);

        File outputDir = args.length > 1
                ? new File(args[1])
                : new File("reports/output");

        try {
            generatePdf(
                    xml,
                    new File(outputDir, "music_groups.pdf")
            );

            generateHtml(
                    xml,
                    new File(outputDir, "music_groups.html")
            );

            System.out.println(
                    "Отчёты созданы в: "
                            + outputDir.getAbsolutePath()
            );

        } catch (Exception e) {

            System.err.println(
                    "Ошибка генерации отчётов: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}
