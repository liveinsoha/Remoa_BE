package Remoa.BE.exception.response;

import Remoa.BE.exception.CustomMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseException extends RuntimeException{
    public final CustomMessage customMessage;
}
