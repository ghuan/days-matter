{
  "entityName": "Dic",
  "comment": "字典明细",
  "sort": "DIC_CODE,SORT,CODE",
  "queryItems": [
      [{"ref": "DIC_CODE"},{"ref": "DIC_NAME"}],[{"ref": "CODE"},{"ref": "NAME"},{"ref": "PY"},{"ref": "WB"}]
    ],
  "items": [
    {
      "text": "字典编码",
      "id": "DIC_CODE",
      "width": 100,
      "length": 150,
      "notNull": true,
      "fixed": true,
      "pkey": true
    },
    {
      "text": "字典名称",
      "id": "DIC_NAME",
      "width": 150,
      "length": 150,
      "notNull": true,
      "fixed": true
    },
    {
      "text": "编码",
      "id": "CODE",
      "width": 150,
      "length": 50,
      "pkey": true,
      "notNull": true,
      "fixed": true
    },
    {
      "text": "名称",
      "id": "NAME",
      "width": 150,
      "notNull": true,
      "length": 50
    },
    {
      "text": "上级编码",
      "id": "PARENT_CODE",
      "width": 150,
      "length": 50
    },
    {
      "text": "排序",
      "id": "SORT",
      "width": 80,
      "type": "int"
    },
    {
      "text": "拼音",
      "id": "PY",
      "display": "none"
    },
    {
      "text": "五笔",
      "id": "WB",
      "display": "none"
    },
    {
      "text": "编码1",
      "id": "CODE1",
      "display": "none"
    },
    {
      "text": "编码2",
      "id": "CODE2",
      "display": "none"
    }
  ]
}
