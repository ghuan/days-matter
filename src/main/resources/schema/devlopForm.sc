{
  "entityName": "Menu",
  "comment": "开发测试form",
  "sort": "sort",
  "cnd": [
    "eq",
    [
      "$",
      "del_flag"
    ],
    [
      "d",
      "0"
    ]
  ],
  "items": [
    {
      "text": "菜单ID",
      "id": "menu_id",
      "type": "int",
      "width": 80,
      "length": 11,
      "display": "none",
      "pkey": true
    },
    {
      "text": "simpletree",
      "id": "simpletree",
      "dic": {
        "id": "dicTreeTest",
        "type": "tree"
      }
    },
    {
      "text": "querytree",
      "id": "querytree",
      "dic": {
        "id": "platform.dicQueryTreeTest",
        "type": "tree"
      }
    },
    {
      "text": "checkbox",
      "id": "checkbox",
      "dic": {"id": "platform.sys_dic","cnd": ["eq",["$","DIC_CODE"],["s","platform.dicType"]],"type": "checkbox"},
      "defaultValue": 1
    },
    {
      "text": "radio",
      "id": "radio",
      "type": "radio",
      "dic": {"id": "platform.sys_dic","cnd": ["eq",["$","DIC_CODE"],["s","platform.dicType"]],"type": "radio"},
      "defaultValue": 0
    },
    {
      "text": "date",
      "id": "date",
      "type": "date"
    },
    {
      "text": "datetime",
      "id": "datetime",
      "type": "datetime"
    }
  ]
}
