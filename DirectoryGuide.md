##  ✅ Feature(도메인) 별로 관리합니다.
##  ✅ 엔티티는 Common에서 관리합니다.

``````
src/main/java/com/yourcompany/project
├── common
│   └── entity
│       ├── User.java
│       ├── Address.java
│       └── ...
├── feature1
│   ├── controller
│   │   └── Feature1Controller.java
│   ├── service
│   │   └── Feature1Service.java
│   ├── repository
│   │   └── Feature1Repository.java
│   └── dto
│       └── Feature1RequestDto.java
├── feature2
│   ├── controller
│   ├── service
│   ├── repository
│   └── dto
├── feature3
│   ├── controller
│   ├── service
│   ├── repository
│   └── dto
└── config
└── WebConfig.java
```