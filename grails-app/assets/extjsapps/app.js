/*
 * This file is generated and updated by Sencha Cmd. You can edit this file as
 * needed for your application, but these edits will have to be merged by
 * Sencha Cmd when upgrading.
 */
Ext.application({
    name: 'appMis',
    extend: 'appMis.Application',
    requires: [
        'appMis.view.desktop.App'
    ],
    // The name of the initial view to create. With the classic toolkit this class
    // will gain a "viewport" plugin if it does not extend Ext.Viewport. With the
    // modern toolkit, the main view will be added to the Viewport.
    controllers:[
        'Teacher',
        'Department',
        'Notice',
        'Rules',
        'User',
        'Role',
        'ProcessAdmin',
        'ProcessDiagram',
        'ProcessTask',
        'Requestmap',
        'DynamicTable',
        'Message',
        'Student'
    ],
    stores:[
        'TeacherStore',
        'DepartmentStore',
        'DepartmentQueryStore',
        'DynamicTableStore',
        'DynamicTableQueryStore',
        'NoticeStore',
        'RulesStore',
        'UserStore',
        'RoleStore',
        'RoleQueryStore',
        'RequestmapStore',
        'ProcessDiagramStore',
        'ProcessTaskStore',
        'ProcessHistoryTaskStore',
        'ProcessDefStore',
        'ProcessHistoryDefStore',
        'StudentStore'
    ],
    views:[
        'teacher.TeacherAdd',
        'teacher.TeacherEdit',
        'teacher.TeacherViewport',
        'teacher.TeacherGridViewport',
        'notice.NoticeAdd',
        'notice.NoticeEdit',
        'notice.NoticeViewport',
        'notice.NoticeGridViewport',
        'notice.ExportNotice',
        'notice.ImportNotice',
        'notice.NoticeMainpageViewport',
        'rules.RulesAdd',
        'rules.RulesEdit',
        'rules.RulesViewport',
        'rules.RulesGridViewport',
        'rules.ExportRules',
        'rules.ImportRules',
        'rules.RulesMainpageViewport',
        'user.UserViewport',
        'user.UserGridViewport',
        'user.UserModify' ,
        'user.ModifyPassword',
        'user.UserEdit',
        'user.UserAdd',
        'user.SelectDepartment',
        'role.RoleViewport',
        'role.RoleGridViewport',
        'role.RoleEdit',
        'role.RoleAdd',
        'processAdmin.ProcessAdminViewport',
        'processAdmin.ProcessAdminGridViewport',
        'processAdmin.ProcessAdminContentViewport',
        'processDiagram.ProcessDiagramViewport',
        'processTask.ProcessTaskViewport',
        'processTask.ProcessHistoryTaskViewport',
        'requestmap.RequestmapViewport',
        'requestmap.RequestmapGridViewport',
        'requestmap.RequestmapEdit',
        'requestmap.RequestmapAdd',
        'department.DepartmentMainpageViewport',
        'department.DepartmentJxkhGridViewport',
        'department.DepartmentViewport',
        'department.DepartmentGridViewport',
        'department.DepartmentEdit',
        'department.DepartmentAdd',
        'dynamicTable.DynamicTableAdd' ,
        'dynamicTable.DynamicTableEdit' ,
        'dynamicTable.DynamicTableViewport' ,
        'dynamicTable.DynamicTableGridViewport',
        'student.StudentAdd',
        'student.StudentEdit',
        'student.StudentViewport',
        'student.StudentGridViewport',
    ],
    mainView: 'appMis.view.desktop.App'
    //-------------------------------------------------------------------------
    // Most customizations should be made to appMis.Application. If you need to
    // customize this file, doing so below this section reduces the likelihood
    // of merge conflicts when upgrading to new versions of Sencha Cmd.
    //-------------------------------------------------------------------------
});

