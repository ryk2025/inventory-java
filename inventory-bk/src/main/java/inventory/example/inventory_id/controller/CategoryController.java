package inventory.example.inventory_id.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inventory.example.inventory_id.dto.CategoryDto;
import inventory.example.inventory_id.model.Item;
import inventory.example.inventory_id.request.CategoryRequest;
import inventory.example.inventory_id.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/category")
public class CategoryController extends BaseController {
  @Autowired
  private CategoryService categoryService;
  // TODO :change to the actual userId
  int userId = 1111;

  @GetMapping()
  public ResponseEntity<List<CategoryDto>> getAllCategories(@RequestParam("user_id") Integer userId) {
    try {
      List<CategoryDto> categories = categoryService.getAllCategories(userId);
      return ResponseEntity.ok(categories);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/items")
  public ResponseEntity<Optional<List<Item>>> getCategoryItems(@RequestParam UUID categoryId) {
    Optional<List<Item>> items = categoryService.getCategoryItems(userId, categoryId);
    return ResponseEntity.ok(items);
  }

  @PostMapping()
  public ResponseEntity<Object> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
    try {
      categoryService.createCategory(categoryRequest, userId);
      return response(HttpStatus.CREATED, "カテゴリーが作成されました");
    } catch (IllegalArgumentException e) {
      return response(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (Exception e) {
      return response(HttpStatus.INTERNAL_SERVER_ERROR, "サーバーエラーが発生しました");
    }
  }

  @PutMapping()
  public ResponseEntity<Object> putMethodName(@PathParam("category_id") UUID category_id,
      @RequestBody CategoryRequest categoryRequest) {
    try {
      categoryService.updateCategory(category_id, categoryRequest, userId);
      return response(HttpStatus.OK, "カテゴリーが更新されました");
    } catch (IllegalArgumentException e) {
      return response(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (Exception e) {
      return response(HttpStatus.INTERNAL_SERVER_ERROR, "サーバーエラーが発生しました");
    }
  }

  @DeleteMapping()
  public ResponseEntity<Object> deleteCategory(@PathParam("category_id") UUID category_id) {
    try {
      categoryService.deleteCategory(category_id, userId);
      return response(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }
}
