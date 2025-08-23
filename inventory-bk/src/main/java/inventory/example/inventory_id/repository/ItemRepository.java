package inventory.example.inventory_id.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inventory.example.inventory_id.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
  List<Item> findByUserIdInAndCategory_NameAndDeletedFlagFalse(
      List<Integer> userIds,
      String categoryName);

}
