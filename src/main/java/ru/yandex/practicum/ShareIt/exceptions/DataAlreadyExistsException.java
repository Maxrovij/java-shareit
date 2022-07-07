package ru.yandex.practicum.ShareIt.exceptions;

import javax.xml.crypto.Data;

public class DataAlreadyExistsException extends RuntimeException {
    public DataAlreadyExistsException(String message){
        super(message);
    }
}
