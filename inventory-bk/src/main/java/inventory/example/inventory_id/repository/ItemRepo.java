package inventory.example.inventory_id.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inventory.example.inventory_id.model.Item;

@Repository
public interface ItemRepo extends JpaRepository<Item, UUID> {
  Optional<List<Item>> findByUserIdInAndCategory_Name(List<Integer> userIds, String categoryName);

  Optional<Item> findByUserIdInAndId(List<Integer> userIds, UUID itemId);
}
