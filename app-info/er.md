
## ER図
```mermaid
erDiagram
  EXTERNAL_FIREBASE_USER {
    uuid id PK
  }

    ITEM {
        string id PK "アイテムID(主キー)"
        string name "アイテム名"
        int quantity "アイテム数量"
        string category_id FK "カテゴリID(外部キー)"
        uuid user_id "ユーザーID"
        boolean deleted_flag "削除フラグ"
        datetime updated_at "更新日時"
    }
    
    CATEGORY {
        uuid id PK "カテゴリID(主キー)"
        string name "カテゴリ名"
        uuid user_id  "ユーザーID"
        boolean deleted_flag "削除フラグ"
    }

    USER ||--o{ CATEGORY : "作成"
    USER ||--o{ ITEM : "作成"
    ITEM }o--|| CATEGORY : "属する"
```


## DBML
```
Table EXTERNAL_FIREBASE_USER {
  id string [pk]
}

Table category {
  id string [pk]
  name string
  user_id string [ref: > EXTERNAL_FIREBASE_USER.id]
  deleted_flag boolean
}

Table item {
  id string [pk]
  name string
  quantity int
  category_id string [ref: > category.id]
  user_id string [ref: > EXTERNAL_FIREBASE_USER.id, not null]
  deleted_flag boolean
  updated_at datetime
}
```