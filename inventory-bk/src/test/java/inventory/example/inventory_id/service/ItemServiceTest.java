package inventory.example.inventory_id.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import inventory.example.inventory_id.model.Category;
import inventory.example.inventory_id.model.Item;
import inventory.example.inventory_id.repository.CategoryRepo;
import inventory.example.inventory_id.repository.ItemRepo;
import inventory.example.inventory_id.request.ItemRequest;

class ItemServiceTest {

  @Mock
  private CategoryRepo categoryRepository;

  @Mock
  private ItemRepo itemRepository;

  @InjectMocks
  private ItemService itemService;

  private int defaultUserId = 111;

  private int defaultSystemUserId = 999;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(itemService, "systemUserId", defaultSystemUserId);
  }

  @Test
  @DisplayName("アイテム作成成功")
  void testCreateItemSuccess() {
    int userId = defaultUserId;
    int systemUserId = defaultSystemUserId;
    String categoryName = "Laptop";
    String itemName = "Notebook";
    int quantity = 5;

    Category category = new Category(categoryName);
    category.setUserId(userId);
    category.setItems(new ArrayList<>());

    ItemRequest request = new ItemRequest();
    request.setName(itemName);
    request.setQuantity(quantity);
    request.setCategoryName(categoryName);

    when(categoryRepository.findByUserIdInAndName(List.of(userId, systemUserId), categoryName))
        .thenReturn(List.of(category));

    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    assertDoesNotThrow(() -> itemService.createItem(userId, request));
    verify(categoryRepository).save(any(Category.class));
  }

  @Test
  @DisplayName("アイテム作成失敗 - カテゴリーが見つからない")
  void testCreateItemCategoryNotFound() {
    int userId = defaultUserId;
    int systemUserId = defaultSystemUserId;
    String categoryName = "Books";

    ItemRequest request = new ItemRequest();
    request.setName("Notebook");
    request.setQuantity(5);
    request.setCategoryName(categoryName);

    when(categoryRepository.findByUserIdInAndName(List.of(userId, systemUserId), categoryName))
        .thenReturn(List.of());

    Exception ex = assertThrows(IllegalArgumentException.class, () -> itemService.createItem(userId, request));
    assertEquals("カテゴリーが見つかりません", ex.getMessage());
  }

  @Test
  @DisplayName("アイテム作成失敗 - 同じ名前のアイテムが存在する")
  void testCreateItemAlreadyExists() {
    int userId = defaultUserId;
    int systemUserId = defaultSystemUserId;
    String categoryName = "Laptop";
    String itemName = "Notebook";

    Category category = new Category(categoryName);
    category.setUserId(userId);

    Item existingItem = new Item();
    existingItem.setName(itemName);
    category.setItems(List.of(existingItem));

    ItemRequest request = new ItemRequest();
    request.setName(itemName);
    request.setQuantity(5);
    request.setCategoryName(categoryName);

    when(categoryRepository.findByUserIdInAndName(List.of(userId, systemUserId), categoryName))
        .thenReturn(List.of(category));

    Exception ex = assertThrows(IllegalArgumentException.class, () -> itemService.createItem(userId, request));
    assertEquals("そのアイテム名は既に登録されています", ex.getMessage());
  }
}