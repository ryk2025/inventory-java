package inventory.example.inventory_id.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import inventory.example.inventory_id.model.Category;
import inventory.example.inventory_id.model.Item;

@DataJpaTest
class ItemRepositoryTest {

  @Autowired
  private ItemRepository itemRepo;

  @Autowired
  private CategoryRepo categoryRepo;

  @Test
  @DisplayName("findByUserIdInAndCategory_Name 正しいアイテムを取得できる")
  void testFindByUserIdInAndCategoryName() {
    // Setup category
    Category category = new Category();
    category.setName("Laptop");
    category.setUserId(123);
    categoryRepo.save(category);

    // Setup item
    Item item = new Item();
    item.setName("Notebook");
    item.setUserId(123);
    item.setCategory(category);
    item.setQuantity(5);
    itemRepo.save(item);

    // Test
    List<Item> result = itemRepo.findByUserIdInAndCategory_Name(List.of(123), "Laptop");
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Notebook");
    assertThat(result.get(0).getCategoryName()).isEqualTo("Laptop");
  }
}
