package inventory.example.inventory_id.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {
  @NotBlank(message = "カテゴリ名は必須")
  @Size(max = 50, message = "カテゴリ名は50文字以内")
  private String name;
}
