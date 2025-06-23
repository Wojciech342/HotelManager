package pl.wojtek.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<String> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        String message = e.getMessage();
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RoomHasActiveReservationsException.class)
    public ResponseEntity<String> handleRoomHasActiveReservations(RoomHasActiveReservationsException e) {
        String message = e.getMessage();
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RoomReservationDatesOverlapException.class)
    public ResponseEntity<String> handleRoomReservationDatesOverlapException(RoomReservationDatesOverlapException e) {
        String message = e.getMessage();
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }
}