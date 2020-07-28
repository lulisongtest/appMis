package com.app

class DynamicTable {
    String tableNameId
    String tableName
    String fieldNameId
    String fieldName
    String fieldType
    int fieldLength
  //  String fieldCalculate

    static constraints = {
        tableNameId()
        tableName()
        fieldNameId()
        fieldName()
        fieldType()
        fieldLength()
    //    fieldCalculate()
    }

}
