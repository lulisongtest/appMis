Ext.define('appMis.model.RequestmapModel', {
    extend: 'Ext.data.Model',
    requires: [
        'Ext.data.Field'
    ],
    fields: ['id', 'url', 'configAttribute', 'httpMethod',"chineseUrl","roleList","treeId","glyph"]
});