package inventory.example.inventory_id.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import inventory.example.inventory_id.dto.ItemDto;
import inventory.example.inventory_id.request.ItemRequest;
import inventory.example.inventory_id.service.ItemService;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

  @Mock
  private ItemService itemService;

  @Spy
  @InjectMocks
  private ItemController itemController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper = new ObjectMapper();

  private int testUserId = 111;
  private String categoryNotFoundMsg = "カテゴリーが見つかりません";

  @BeforeEach
  void setUp() {
    doReturn(testUserId).when(itemController).fetchUserIdFromToken();
    mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
  }

  @Test
  @Tag("GET: /api/item")
  @DisplayName("アイテム一覧取得-200 OK")
  void getItems_success() throws Exception {
    List<ItemDto> items = Arrays.asList(new ItemDto(), new ItemDto());
    when(itemService.getItems(anyInt(), anyString())).thenReturn(items);
    mockMvc.perform(get("/api/item").param("category_name", "test"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(items)));
  }

  @Test
  @Tag("POST: /api/item")
  @DisplayName("アイテム作成-201 Created")
  void createItem_success() throws Exception {
    ItemRequest req = new ItemRequest();
    req.setName("itemName");
    req.setCategoryName("category");
    req.setQuantity(1);
    doNothing().when(itemService).createItem(anyInt(), any(ItemRequest.class));
    mockMvc.perform(post("/api/item")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(content().json("{\"message\":\"アイテムの作成が完了しました\"}"));
  }

  @Test
  @Tag("POST: /api/item")
  @DisplayName("アイテム作成-400 Bad Request カテゴリーが見つからない")
  void createItem_badRequest_categoryNotFound() throws Exception {
    ItemRequest req = new ItemRequest();
    req.setName("itemName");
    req.setCategoryName("category");
    req.setQuantity(1);
    doThrow(new IllegalArgumentException(categoryNotFoundMsg)).when(itemService).createItem(anyInt(),
        any(ItemRequest.class));
    mockMvc.perform(post("/api/item")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest())
        .andExpect(content().json("{\"message\":\"" + categoryNotFoundMsg + "\"}"));
  }

  @Test
  @Tag("POST: /api/item")
  @DisplayName("アイテム作成-400 Bad Request アイテム名が重複")
  void createItem_badRequest_itemNameDuplicate() throws Exception {
    ItemRequest req = new ItemRequest();
    req.setName("itemName");
    req.setCategoryName("category");
    req.setQuantity(1);
    doThrow(new IllegalArgumentException(String.format("アイテム名 '%s' は既に存在します", req.getName())))
        .when(itemService)
        .createItem(anyInt(),
            any(ItemRequest.class));
    mockMvc.perform(post("/api/item")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest())
        .andExpect(content().json("{\"message\":\"" + String.format("アイテム名 '%s' は既に存在します", req.getName()) + "\"}"));
  }

  @Test
  @Tag("PUT: /api/item")
  @DisplayName("アイテム更新-200 OK")
  void updateItem_success() throws Exception {
    UUID itemId = UUID.randomUUID();
    ItemRequest req = new ItemRequest();
    req.setName("itemName");
    req.setCategoryName("category");
    req.setQuantity(1);
    doNothing().when(itemService).updateItem(anyInt(), eq(itemId), any(ItemRequest.class));
    mockMvc.perform(put("/api/item")
        .param("item_id", itemId.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"message\":\"アイテムの更新が完了しました\"}"));
  }

  @Test
  @Tag("PUT: /api/item")
  @DisplayName("アイテム更新-404 Not Found アイテムが見つかりません")
  void updateItem_notFound() throws Exception {
    UUID itemId = UUID.randomUUID();
    ItemRequest req = new ItemRequest();
    req.setName("itemName");
    req.setCategoryName("category");
    req.setQuantity(1);
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "アイテムが見つかりません")).when(itemService).updateItem(
        anyInt(),
        eq(itemId),
        any(ItemRequest.class));
    mockMvc.perform(put("/api/item")
        .param("item_id", itemId.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\"message\":\"アイテムが見つかりません\"}"));
  }

  @Test
  @Tag("PUT: /api/item")
  @DisplayName("アイテム更新-400 Bad Request アイテム名は既に登録されています")
  void updateItem_badRequest() throws Exception {
    UUID itemId = UUID.randomUUID();
    ItemRequest req = new ItemRequest();
    req.setName("itemName");
    req.setCategoryName("category");
    req.setQuantity(1);
    doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "アイテム名は既に登録されています")).when(itemService).updateItem(
        anyInt(),
        eq(itemId),
        any(ItemRequest.class));
    mockMvc.perform(put("/api/item")
        .param("item_id", itemId.toString())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest())
        .andExpect(content().json("{\"message\":\"アイテム名は既に登録されています\"}"));
  }

  @Test
  @Tag("DELETE: /api/item")
  @DisplayName("アイテム削除-202 Accepted")
  void deleteItem_success() throws Exception {
    UUID itemId = UUID.randomUUID();
    doNothing().when(itemService).deleteItem(anyInt(), eq(itemId));
    mockMvc.perform(delete("/api/item")
        .param("item_id", itemId.toString()))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"message\":\"アイテムの削除が完了しました\"}"));
  }

  @Test
  @Tag("DELETE: /api/item")
  @DisplayName("アイテム削除-404 Not Found")
  void deleteItem_notFound() throws Exception {
    UUID itemId = UUID.randomUUID();
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, categoryNotFoundMsg)).when(itemService).deleteItem(
        anyInt(),
        eq(itemId));
    mockMvc.perform(delete("/api/item")
        .param("item_id", itemId.toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\"message\":\"" + categoryNotFoundMsg + "\"}"));
  }
}
