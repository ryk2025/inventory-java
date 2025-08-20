package inventory.example.inventory_id.controller;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {
  protected ResponseEntity<Object> response(HttpStatus status) {
    return ResponseEntity.status(status).build();
  }

  protected ResponseEntity<Object> response(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(Collections.singletonMap("message", message));
  }

  protected ResponseEntity<Object> response(HttpStatus status, Object data) {
    return ResponseEntity.status(status).body(data);
  }
}