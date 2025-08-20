package inventory.example.inventory_id.service;

import inventory.example.inventory_id.dto.CategoryDto;

import inventory.example.inventory_id.model.Category;
import inventory.example.inventory_id.model.Item;
import inventory.example.inventory_id.repository.CategoryRepo;
import inventory.example.inventory_id.request.CategoryRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {
  @Autowired
  private CategoryRepo categoryRepo;

  @Value("${system.userid}")
  private int systemUserId;

  public List<CategoryDto> getAllCategories(int userId) {
    return categoryRepo.findByUserIdIn(List.of(userId, systemUserId)).stream()
        .sorted(Comparator.comparing(Category::getName))
        .map(category -> {
          CategoryDto dto = new CategoryDto();
          dto.setName(category.getName());
          dto.setItems(category.getItems());
          return dto;
        })
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

    // Check if user already has 50 categories (excluding deleted)
    long count = categoryRepo.countByUserIdAndDeletedFlagFalse(userId);
    if (count >= 50) {
      throw new IllegalArgumentException("登録できるカテゴリの上限に達しています");
    }

    if (categoryRepo.existsByUserIdAndName(userId, categoryRequest.getName())) {
      Optional<Category> existing = categoryRepo.findByUserIdAndName(userId, categoryRequest.getName());
      if (existing.isPresent() && existing.get().isDeletedFlag()) {
        // カテゴリーが存在し、削除フラグが立っている場合は復活させる
        existing.get().setDeletedFlag(false);
        existing.get().setItems(new ArrayList<>());
        return categoryRepo.save(existing.get());
      }
      throw new IllegalArgumentException("カテゴリー名はすでに存在します");
    }
    Category category = new Category();
    category.setName(categoryRequest.getName());
    category.setUserId(userId);
    category.setDeletedFlag(false);
    return categoryRepo.save(category);
  }

  public Category updateCategory(UUID categoryId, CategoryRequest categoryRequest, int userId) {
    Optional<Category> categoryOpt = categoryRepo.findByUserIdAndId(userId, categoryId);
    if (!categoryOpt.isPresent()) {
      throw new IllegalArgumentException("カテゴリーが見つかりません");
    }
    Category category = categoryOpt.get();
    category.setName(categoryRequest.getName());
    categoryRepo.save(category);
    return category;
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
