package inventory.example.inventory_id.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import inventory.example.inventory_id.model.Category;
import inventory.example.inventory_id.repository.CategoryRepository;

@Component
public class DataInitializer implements CommandLineRunner {
  @Autowired
  private CategoryRepository categoryRepo;
  @Value("${system.userid}")
  private int systemUserId;

  @Override
  public void run(String... args) {
    addDefaultCategories();
  }

  private void addDefaultCategories() {
    List<String> categoryNames = Arrays.asList("Children", "Food", "Electronics", "Books", "Kitchen");
    for (String name : categoryNames) {
      boolean exists = categoryRepo.existsByUserIdAndName(systemUserId, name);
      if (!exists) {
        Category category = new Category(name);
        category.setUserId(systemUserId);
        categoryRepo.save(category);
      }
    }
  }
}
