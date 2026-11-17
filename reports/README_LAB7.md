# Лабораторная работа № 7 — отчёты PDF и HTML

## Что добавлено
- `templates/music_groups_pdf.jrxml` — табличный отчёт для PDF.
- `templates/music_groups_html.jrxml` — карточки музыкальных групп для HTML.
- `src/edu/java/app/reports/ReportManager.java` — компиляция шаблонов, чтение XML и экспорт.
- `examples/music_groups_sample.xml` — тестовые данные в формате, который использует `XmlFileManager`.

## Формат входного XML
Шаблоны читают `/musicGroups/group` и атрибуты `name`, `rate`, `hit`, `year`, `leader`. Это соответствует XML, который сохраняет `edu.java.app.files.XmlFileManager`.

## Подключение в Eclipse
1. Скачайте JasperReports 5.0.0 и необходимые зависимости из комплекта iReport 5.0.0.
2. В Eclipse откройте **Project → Properties → Java Build Path → Libraries → Add External JARs**.
3. Добавьте JasperReports и все JAR-файлы из каталога библиотек iReport. Не добавляйте только один основной JAR: библиотека использует зависимости.
4. Убедитесь, что корнем проекта является каталог, в котором находятся папки `src` и `reports`. Шаблоны загружаются по относительным путям от рабочей директории запуска.

## Запуск
Из корня проекта:
```bash
java edu.java.app.reports.ReportManager reports/examples/music_groups_sample.xml reports/output
```
В Eclipse можно запустить `ReportManager` как Java Application и указать эти аргументы во вкладке **Run Configurations → Arguments → Program arguments**.

Результат:
- `reports/output/music_groups.pdf`
- `reports/output/music_groups.html`

Для отчёта по собственным данным вместо примера укажите XML, экспортированный из приложения.

## Важно
Шаблоны созданы в формате JRXML для старой ветки JasperReports/iReport 5.0.0. Перед сдачей их нужно открыть в iReport Designer, проверить визуальное отображение и сохранить скриншоты конструктора. В этой среде не выполнялась компиляция JasperReports: для окончательной проверки нужны JAR-зависимости из вашей установки iReport.
