package inventory.example.inventory_id.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello extends BaseController {

  @GetMapping("/health")
  public String health() {
    return "Health is good";
  }
}
