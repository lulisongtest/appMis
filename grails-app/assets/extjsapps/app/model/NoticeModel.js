Ext.define('appMis.model.NoticeModel', {
 extend: 'Ext.data.Model', 
requires: ['Ext.data.Field'],
fields: [
'id','title','titleCode','jb','dep','scr' ,
{name : 'scDate', dateFormat: 'Y-m-d',type : 'date'}
,'wjlx'
]});

