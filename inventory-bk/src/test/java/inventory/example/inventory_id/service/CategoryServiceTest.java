package inventory.example.inventory_id.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import inventory.example.inventory_id.model.Category;
import inventory.example.inventory_id.model.Item;
import inventory.example.inventory_id.repository.CategoryRepository;
import inventory.example.inventory_id.request.CategoryRequest;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepo;

  @InjectMocks
  private CategoryService categoryService;
  private int defaultUserId = 111;

  private int defaultSystemUserId = 999;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(categoryService, "systemUserId", defaultSystemUserId);
  }

  @Test
  @DisplayName("カテゴリー作成成功")
  void testCreateCategorySuccess() {
    CategoryRequest request = new CategoryRequest();
    request.setName("TestCategory");
    int userId = defaultUserId;

    when(categoryRepo.findByUserIdIn(List.of(userId, defaultSystemUserId))).thenReturn(List.of());

    Category savedCategory = new Category();
    savedCategory.setName(request.getName());
    savedCategory.setUserId(userId);

    when(categoryRepo.save(any(Category.class))).thenReturn(savedCategory);

    Category result = categoryService.createCategory(request, userId);

    assertEquals(request.getName(), result.getName());
    assertEquals(userId, result.getUserId());
  }

  @Test
  @DisplayName("カテゴリー名がすでに存在する場合のエラー")
  void testCreateCategoryAlreadyExists() {
    CategoryRequest request = new CategoryRequest();
    request.setName("TestCategory");
    int userId = defaultUserId;
    Category savedCategory = new Category();
    savedCategory.setName(request.getName());
    savedCategory.setUserId(userId);
    when(categoryRepo.findByUserIdIn(List.of(userId, defaultSystemUserId))).thenReturn(List.of(savedCategory));
    Exception exception = assertThrows(ResponseStatusException.class, () -> {
      categoryService.createCategory(request, userId);
    });

    assertEquals("カテゴリー名はすでに存在します", ((ResponseStatusException) exception).getReason());
  }

  @Test
  @DisplayName("削除済みカテゴリーの再作成")
  void testCreateCategoryAlreadyDeleted() {
    CategoryRequest request = new CategoryRequest();
    request.setName("TestCategory");
    int userId = defaultUserId;
    Category existedCategory = new Category();
    existedCategory.setName(request.getName());
    existedCategory.setUserId(userId);
    existedCategory.setDeletedFlag(true);

    when(categoryRepo.findByUserIdIn(List.of(userId, defaultSystemUserId))).thenReturn(List.of(existedCategory));

    Category newCategory = new Category();
    newCategory.setName(request.getName());
    newCategory.setUserId(userId);
    when(categoryRepo.save(any(Category.class))).thenReturn(newCategory);

    Category result = categoryService.createCategory(request, userId);
    assertFalse(result.isDeletedFlag());
  }

  @Test
  @DisplayName("登録できるカテゴリの上限に達している場合のエラー")
  void testCreateCategoryLimitExceeded() {
    CategoryRequest request = new CategoryRequest();
    request.setName("TestCategory");
    int userId = defaultUserId;

    // Create a list with 50 categories for the user
    List<Category> existingCategories = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      Category category = new Category();
      category.setUserId(userId);
      category.setName("Category" + i);
      existingCategories.add(category);
    }

    // Mock the repository to return the list
    when(categoryRepo.findByUserIdIn(List.of(userId, defaultSystemUserId))).thenReturn(existingCategories);

    Exception exception = assertThrows(ResponseStatusException.class, () -> {
      categoryService.createCategory(request, userId);
    });

    assertEquals("登録できるカテゴリの上限に達しています", ((ResponseStatusException) exception).getReason());
  }

  @Test
  @DisplayName("カテゴリーアップデート成功")
  void testUpdateCategorySuccess() {
    UUID categoryId = UUID.randomUUID();
    CategoryRequest request = new CategoryRequest();
    request.setName("UpdatedName");
    int userId = 1;

    Category category = new Category("OldName");
    category.setUserId(userId);
    when(categoryRepo.findByUserIdAndId(userId, categoryId)).thenReturn(Optional.of(category));
    when(categoryRepo.save(any(Category.class))).thenReturn(category);

    Category result = categoryService.updateCategory(categoryId, request, userId);
    assertEquals("UpdatedName", result.getName());
  }

  @Test
  @DisplayName("アップデートしたいデータがない場合のエラー")
  void testUpdateCategoryNotFound() {
    UUID categoryId = UUID.randomUUID();
    CategoryRequest request = new CategoryRequest();
    request.setName("UpdatedName");
    int userId = 1;

    when(categoryRepo.findByUserIdAndId(userId, categoryId)).thenReturn(Optional.empty());

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      categoryService.updateCategory(categoryId, request, userId);
    });

    assertEquals("カテゴリーが見つかりません", exception.getMessage());
  }

  @Test
  @DisplayName("カテゴリー削除成功")
  void testDeleteCategorySuccess() {
    UUID categoryId = UUID.randomUUID();
    int userId = defaultUserId;
    Category category = new Category();
    category.setId(categoryId);
    category.setUserId(userId);
    category.setItems(new ArrayList<>());
    when(categoryRepo.findByUserIdIn(List.of(userId, defaultSystemUserId))).thenReturn(List.of(category));

    assertDoesNotThrow(() -> categoryService.deleteCategory(categoryId, userId));
    assertTrue(category.isDeletedFlag());
  }

  @Test
  @DisplayName("カテゴリー削除時にアイテムが存在する場合のエラー")
  void testDeleteCategoryHasItems() {
    UUID categoryId = UUID.randomUUID();
    int userId = defaultUserId;
    Category category = new Category();
    category.setId(categoryId);
    category.setUserId(userId);
    category.setItems(new ArrayList<>(List.of(new Item())));
    when(categoryRepo.findByUserIdIn(List.of(userId, defaultSystemUserId))).thenReturn(List.of(category));

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      categoryService.deleteCategory(categoryId, userId);
    });

    assertEquals("アイテムが存在するため削除できません", exception.getMessage());
  }
}
