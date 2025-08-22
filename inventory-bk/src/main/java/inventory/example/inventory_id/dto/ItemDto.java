package inventory.example.inventory_id.dto;

import lombok.Data;

@Data
public class ItemDto {
  private String name;
  private int quantity;
  private String categoryName;
}
