
## ER図
```mermaid
erDiagram
    USER {
        string id PK
        string email
        string name
    }

    ITEM {
        string id PK
        string name
        int quantity
        string category_id FK
        string user_id FK
        boolean deleted_flag
        datetime updated_at
    }
    
    CATEGORY {
        string id PK
        string name
        string user_id FK
        boolean deleted_flag
    }

    USER ||--o{ CATEGORY : "作成"
    USER ||--o{ ITEM : "作成"
    ITEM ||--|| USER : "属する"
    ITEM }o--|| CATEGORY : "属する"
```


## DBML
```
Table user {
  id string [pk]
  email string
  name string
}

Table category {
  id string [pk]
  name string
  user_id string [ref: > user.id]
  deleted_flag boolean
}

Table item {
  id string [pk]
  name string
  quantity int
  category_id string [ref: > category.id]
  user_id string [ref: > user.id, not null]
  deleted_flag boolean
  updated_at datetime
}
```