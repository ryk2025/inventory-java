package inventory.example.inventory_id.dto;

import java.util.List;

import inventory.example.inventory_id.model.Item;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
public class CategoryDto {
  private String name;
  private List<Item> items;

  public CategoryDto(String name, List<Item> items) {
    this.name = name;
    this.items = items;
  }
}