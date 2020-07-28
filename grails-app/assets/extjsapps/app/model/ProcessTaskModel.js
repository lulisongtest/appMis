Ext.define('appMis.model.ProcessTaskModel', {
 extend: 'Ext.data.Model', 
requires: ['Ext.data.Field'],
fields: [
    'REV_' ,
    'EXECUTION_ID_' ,
    'PROC_NAME_',
    'PROC_INST_ID_' ,
    'PROC_DEF_ID_' ,
    'NAME_' ,
    'PARENT_TASK_ID_' ,
    'DESCRIPTION_' ,
    'TASK_DEF_KEY_' ,
    'OWNER_' ,
    'ASSIGNEE_' ,
    'DELEGATION_' ,
    'PRIORITY_' ,
    { name : 'CREATE_TIME_',
        dateFormat: 'Y-m-d',
        type : 'date'
    } ,
    { name : 'DUE_DATE_',
        dateFormat: 'Y-m-d',
        type : 'date'
    } ,
    'CATEGORY_' ,
    'SUSPENSION_STATE_' ,
    'TENANT_ID_' ,
    'FORM_KEY_' ,
    { name : 'CLAIM_TIME_',
        dateFormat: 'Y-m-d',
        type : 'date'
    }
]});

