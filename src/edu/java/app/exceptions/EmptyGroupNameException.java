package edu.java.app.exceptions;

/**
 * Исключение, возникающее при попытке оставить название группы пустым.
 * <p>
 * Выбрасывается, если пользователь стирает наименование музыкальной группы
 * или вводит строку, состоящую только из пробельных символов.
 *
 * @author Оганнисян Э. А.
 * @version 1.0
 */
public class EmptyGroupNameException extends Exception {

    /**
     * Конструктор с созданием сообщения об ошибке.
     *
     * @param message подробное описание причины возникновения исключения
     */
    public EmptyGroupNameException(String message) {
        super(message);
    }
}