package pl.cezarysanecki.parkingdomain.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.cezarysanecki.parkingdomain.commons.EntityNotFound;

@RestControllerAdvice
class EntityNotFoundHandler {

  @ExceptionHandler(EntityNotFound.class)
  ProblemDetail handle(EntityNotFound exception) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
  }

}
