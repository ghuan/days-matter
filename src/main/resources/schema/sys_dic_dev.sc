{
  "entityName": "Dic",
  "comment": "字典(开发)",
  "queryItems": [
    [
      {
        "ref": "NAME"
      },
      {
        "ref": "DIC_CODE"
      },
      {
        "ref": "TABLE_NAME"
      },
      {
        "ref": "PY"
      },
      {
        "ref": "WB"
      }
    ]
  ],
  "cnd": [
    "eq",
    [
      "$",
      "DEL"
    ],
    [
      "d",
      "0"
    ]
  ],
  "items": [
    {
      "text": "名称",
      "id": "NAME",
      "width": 100,
      "length": 50,
      "notNull": true,
      "queryable": true
    },
    {
      "text": "编码",
      "id": "DIC_CODE",
      "width": 100,
      "length": 50,
      "pkey": "true",
      "notNull": true,
      "queryable": true,
      "fixed": true
    },
    {
      "text": "类型",
      "id": "TYPE",
      "width": 100,
      "type": "int",
      "notNull": true,
      "dic": {"id": "platform.sys_dic","cnd": ["eq",["$","DIC_CODE"],["s","platform.dicType"]]},
      "length": 150
    },
    {
      "text": "表名",
      "id": "TABLE_NAME",
      "width": 100,
      "length": 50,
      "queryable": true
    },
    {
      "text": "是否实时查询",
      "id": "QUERY_ONLY",
      "display": "form",
      "dic": {
        "id": "platform.yesOrNo",
        "type": "radio"
      },
      "defaultValue": "0"
    },
    {
      "text": "是否分机构",
      "id": "IS_ORG",
      "display": "form",
      "dic": {
        "id": "platform.yesOrNo",
        "type": "radio"
      },
      "defaultValue": "0"
    },
    {
      "text": "拼音",
      "id": "PY",
      "width": 100,
      "length": 50,
      "display": "none",
      "queryable": true
    },
    {
      "text": "五笔",
      "id": "WB",
      "width": 100,
      "length": 50,
      "display": "none",
      "queryable": true
    },
    {
      "text": "是否隐藏",
      "id": "DEL",
      "type": "int",
      "display": "none"
    },
    {
      "text": "创建时间",
      "id": "CREATE_TIME",
      "display": "none"
    },
    {
      "text": "内容",
      "id": "CONFIG",
      "type": "text",
      "height": 400,
      "colspan": 3,
      "display": "form"
    }
  ]
}
