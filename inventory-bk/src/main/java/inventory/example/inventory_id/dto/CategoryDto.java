package inventory.example.inventory_id.dto;

import java.util.List;

import inventory.example.inventory_id.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
  private String name;
  private List<Item> items;
}