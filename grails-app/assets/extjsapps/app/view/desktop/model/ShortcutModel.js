//为了在桌面图标旁显示汉字重新定义ShortcutModel
Ext.define('appMis.view.desktop.model.ShortcutModel', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'name'//可以是汉字
            //convert: Ext.String.createVarName//加入这行不可以显示汉字
        },
        {name: 'nameId'},//这行不可以删除，这是【name】每个窗口的id
        {name: 'iconCls'},
        {name: 'module'}
    ]
});