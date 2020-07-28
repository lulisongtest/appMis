Ext.define('appMis.model.ProcessHistoryTaskModel', {
 extend: 'Ext.data.Model', 
requires: ['Ext.data.Field'],
fields: [
    'ID_',
    'PROC_NAME_',
    'PROC_DEF_ID_',
    'TASK_DEF_KEY_',
    'PROC_INST_ID_',
    'EXECUTION_ID_',
    'NAME_',
    'PARENT_TASK_ID_',
    'DESCRIPTION_',
    'OWNER_',
    'ASSIGNEE_',
    { name : 'START_TIME_',
        dateFormat: 'Y-m-d',
        type : 'date'
    } ,
    { name : 'CLAIM_TIME_',
        dateFormat: 'Y-m-d',
        type : 'date'
    } ,
    { name : 'END_TIME_',
        dateFormat: 'Y-m-d',
        type : 'date'
    } ,
    'DURATION_',
    'DELETE_REASON_',
    'PRIORITY_',
    { name : 'DUE_DATE_',
        dateFormat: 'Y-m-d',
        type : 'date'
    } ,
    'FORM_KEY_',
    'CATEGORY_',
    'TENANT_ID_'
]});

