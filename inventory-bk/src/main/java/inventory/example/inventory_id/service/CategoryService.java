package inventory.example.inventory_id.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import inventory.example.inventory_id.dto.CategoryDto;
import inventory.example.inventory_id.model.Category;
import inventory.example.inventory_id.model.Item;
import inventory.example.inventory_id.repository.CategoryRepo;
import inventory.example.inventory_id.request.CategoryRequest;

@Service
public class CategoryService {
  @Autowired
  private CategoryRepo categoryRepo;

  @Value("${system.userid}")
  private int systemUserId;

  public List<CategoryDto> getAllCategories(int userId) {
    // ユーザとデフォルトのカテゴリを取得
    return categoryRepo.findByUserIdIn(List.of(userId, systemUserId)).stream()
        .sorted(Comparator.comparing(Category::getName))
        .map(category -> new CategoryDto(category.getName(), category.getItems()))
        .toList();
  }

  public Optional<List<Item>> getCategoryItems(int userId, UUID categoryId) {
    Optional<Category> category = categoryRepo.findByUserIdAndId(userId, categoryId);
    if (category.isPresent()) {
      return Optional.of(category.get().getItems());
    }
    return Optional.empty();
  }

  public Category createCategory(CategoryRequest categoryRequest, int userId) {

    // ユーザのカテゴリ数を確認,50以上は登録不可
    if (categoryRepo.countByUserIdAndDeletedFlagFalse(userId) >= 50) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "登録できるカテゴリの上限に達しています");
    }
    // カテゴリー名の重複チェック
    if (categoryRepo.existsByUserIdAndName(userId, categoryRequest.getName())) {
      Optional<Category> existing = categoryRepo.findByUserIdAndName(userId, categoryRequest.getName());
      if (existing.isPresent() && existing.get().isDeletedFlag()) {
        // カテゴリーが存在し、削除フラグが立っている場合は復活させる
        existing.get().setDeletedFlag(false);
        return categoryRepo.save(existing.get());
      }
      throw new ResponseStatusException(HttpStatus.CONFLICT, "カテゴリー名はすでに存在します");
    }
    Category category = new Category(categoryRequest.getName());
    category.setUserId(userId);
    return categoryRepo.save(category);
  }

  public CategoryDto updateCategory(UUID categoryId, CategoryRequest categoryRequest, int userId) {
    Optional<Category> categoryOpt = categoryRepo.findByUserIdAndId(userId, categoryId);
    if (!categoryOpt.isPresent()) {
      throw new IllegalArgumentException("カテゴリーが見つかりません");
    }
    Category category = categoryOpt.get();
    category.setName(categoryRequest.getName());
    Category result = categoryRepo.save(category);
    return new CategoryDto(result.getName(), result.getItems());
  }

  public void deleteCategory(UUID id, int userId) {
    Optional<Category> categoryOpt = categoryRepo.findByUserIdAndId(userId, id);
    if (categoryOpt.isPresent()) {
      Category category = categoryOpt.get();
      if (category.getItems() == null || category.getItems().isEmpty()) {
        // アイテムが存在しない場合のみ削除フラグを立てる
        category.setDeletedFlag(true);
        categoryRepo.save(category);
      } else {
        throw new IllegalArgumentException("アイテムが存在するため削除できません");
      }
    }
  }
}
