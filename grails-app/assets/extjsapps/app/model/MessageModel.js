Ext.define('appMis.model.MessageModel', {
    extend: 'Ext.data.Model',

    requires: [
        'Ext.data.Field'
    ],

    fields: ['id','sendId', 'sendName', 'receiveId','receiveName','content',
        { name : 'messageDate',
            dateFormat: 'Y-m-d',
            type : 'date'
        },
        'fileName','messageGroup','receiveStatus']
});


