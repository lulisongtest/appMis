Ext.define('appMis.model.StudentModel', {
    extend: 'Ext.data.Model',
    requires: [
        'Ext.data.Field'
    ],
    fields: ['id','username', 'truename','email','department','major','college','phone','homephone','idNumber',
        { name : 'birthDate',
            dateFormat: 'Y-m-d',
            type : 'date'
        },
        'sex','race','politicalStatus',
        { name : 'enrollDate',
            dateFormat: 'Y-m-d',
            type : 'date'
        },
        'treeId','currentStatus']
});


