package inventory.example.inventory_id.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
  private CategoryRepository categoryRepo;

  @Test
  @DisplayName("findByUserIdInAndCategory_Name 正しいアイテムを取得できる")
  void testFindByUserIdInAndCategoryName() {
    // Setup category
    Category category = new Category();
    category.setName("PC");
    category.setUserId(123);
    categoryRepo.save(category);

    // Setup item
    Item notDeleteditem = new Item();
    notDeleteditem.setName("Notebook");
    notDeleteditem.setUserId(123);
    notDeleteditem.setCategory(category);
    notDeleteditem.setQuantity(5);
    itemRepo.save(notDeleteditem);
    Item deleteditem = new Item();
    deleteditem.setName("Desktop");
    deleteditem.setUserId(123);
    deleteditem.setCategory(category);
    deleteditem.setQuantity(5);
    deleteditem.setDeletedFlag(true);
    itemRepo.save(deleteditem);

    // Test
    List<Item> result = itemRepo.findByUserIdInAndCategory_NameAndDeletedFlagFalse(List.of(123), "PC");
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Notebook");
    assertThat(result.get(0).getCategoryName()).isEqualTo("PC");
  }

  @Test
  @DisplayName("findByUserIdInAndIdAndDeletedFlagFalse 存在しない場合は空")
  void testFindByUserIdInAndIdAndDeletedFlagFalseNotFound() {
    Optional<Item> result = itemRepo.findByUserIdInAndIdAndDeletedFlagFalse(List.of(999), UUID.randomUUID());
    assertThat(result).isEmpty();
  }
}
