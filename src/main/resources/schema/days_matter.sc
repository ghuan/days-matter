{
  "entityName": "DaysMatter",
  "comment": "纪念日",
  "sort": "id",
  "queryItems": [
      [{"ref": "title"}]
    ],
  "items": [
    {
      "text": "id",
      "id": "id",
      "display": "none",
      "pkey": true
    },
    {
      "text": "标题",
      "id": "title",
      "width": 250,
      "colspan":2,
      "length": 150,
      "notNull": true
    },
     {
       "text": "日期类型",
       "id": "dateType",
       "width": 80,
       "defaultValue":1,
       "exCfg":{"editable":false},
       "dic": {"id": "date_type"},
       "type":"int",
       "notNull": true
     },
    {
      "text": "",
      "id": "date",
      "width": 150,
      "exCfg":{"hidden":true},
      "notNull": true
    },
     {
       "text": "目标日期",
       "id": "dateShow",
       "virtual":true,
       "width": 200,
       "exCfg":{"editable":false},
       "notNull": true
     },
    {
      "text": "置顶",
      "id": "top",
      "width": 80,
      "formExCfg": {
          "xtype": "checkbox"
        },
      "type":"int",
      "renderer": "onRendererYes"
    },
    {
      "text": "Big Day（周年和百日提醒）",
      "id": "bigDay",
      "width": 80,
      "formExCfg": {
          "xtype": "checkbox",
          "labelWidth":170
        },
      "type":"int",
      "renderer": "onRendererYes"
    },
    {
      "text": "正数包含起始日（+1天）",
      "id": "containBeginDate",
      "width": 80,
      "formExCfg": {
          "xtype": "checkbox",
          "labelWidth":150
        },
      "type":"int",
      "renderer": "onRendererYes"
    },
    {
      "text": "重复",
      "id": "repeat",
      "type":"int",
      "defaultValue":0,
      "notNull": true,
      "dic": {"id": "repeat_type"},
      "width": 150
    }
  ]
}
