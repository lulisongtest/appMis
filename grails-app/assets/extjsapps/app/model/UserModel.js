Ext.define('appMis.model.UserModel', {
    extend: 'Ext.data.Model',

    requires: [
        'Ext.data.Field'
    ],

    fields: ['id', 'username', 'password','enabled','accountExpired','accountLocked','passwordExpired',
        {
            name : 'regdate',
            dateFormat: 'Y-m-d H:i:s',
            type : 'date'
        },
        {
            name : 'lastlogindate',
            dateFormat: 'Y-m-d H:i:s',
            type : 'date'
        },
        'truename','chineseAuthority','department','treeId','email','phone']
});
