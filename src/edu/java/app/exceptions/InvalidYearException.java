package edu.java.app.exceptions;

/**
 * Исключение, возникающее при вводе недопустимого года основания музыкальной группы.
 * <p>
 * Выбрасывается в случае, если введённый год основания выходит
 * за пределы допустимого временного интервала (от 1900 до 2026 года).
 *
 * @author Оганнисян Э. А.
 * @version 1.0
 */
public class InvalidYearException extends Exception {

    /**
     * Конструктор с созданием сообщения об ошибке.
     *
     * @param message подробное описание причины возникновения исключения
     */
    public InvalidYearException(String message) {
        super(message);
    }
}