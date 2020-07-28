package com.app
import com.app.DynamicTable
/**
 * Created by Adminlinken on 14-5-31.
 */
class CreateMainpageViewportjs {
    def createMainpageViewportjs(String tablename){
        String query="from DynamicTable where tableNameId='"+ tablename+"'"
        def list = DynamicTable.findAll(query)
        try{
            String filePath="D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\view\\"+tablename
            File myFilePath=new File(filePath);
            if (!myFilePath.exists()){
                myFilePath.mkdir()
            }
        }catch(Exception e){}
        try {
            StringBuilder sb=new StringBuilder();
            sb.append("""
Ext.define('salaryMis.view."""+tablename+"""."""+initcap2(tablename)+"""MainpageViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget."""+tablename+"""mainpageviewport',
    glyph: 'xf015@FontAwesome',
    closable: true,
    icon: 'images/application/application_form.png',
    title: '首页',
    items: [
        {
            xtype: 'tabpanel',
            header: false,
            //height: 200,
            items: [
                {
                    xtype: 'panel',
                    title: '待办事务',
                    html: '在职人员___在职人员__在工资管理系统运行过程中，需要及时地呈报报表，以便上级能及时了解工资管理系统运行状况，做出相应重要的决策。\\r\\n系统主要功能包括报表数据的导入、录入、修改、审核、批准、查询、组合查询、数据导入、备份，生成报表、查询报表、操作报表、打印、报表输出到Excel、Pdf、Html、Word等文件、报表导入、备份等；用户管理包括用户创建、用户删除、用户权限分配管理等。\\r\\n ',
                },
                {
                    xtype: 'panel',
                    title: '已办事务',
                    html: '在职人员___在职人员__在工资管理系统运行过程中，需要及时地呈报报表，以便上级能及时了解工资管理系统运行状况，做出相应重要的决策。\\r\\n系统主要功能包括报表数据的导入、录入、修改、审核、批准、查询、组合查询、数据导入、备份，生成报表、查询报表、操作报表、打印、报表输出到Excel、Pdf、Html、Word等文件、报表导入、备份等；用户管理包括用户创建、用户删除、用户权限分配管理等。\\r\\n ',
                },
                {
                    xtype: 'panel',
                    title: '我的事务',
                    html: '在职人员___在职人员__在工资管理系统运行过程中，需要及时地呈报报表，以便上级能及时了解工资管理系统运行状况，做出相应重要的决策。\\r\\n系统主要功能包括报表数据的导入、录入、修改、审核、批准、查询、组合查询、数据导入、备份，生成报表、查询报表、操作报表、打印、报表输出到Excel、Pdf、Html、Word等文件、报表导入、备份等；用户管理包括用户创建、用户删除、用户权限分配管理等。\\r\\n ',
                },
                {
                    xtype: 'panel',
                    title: '新建事务',
                    html: '在职人员___在职人员__在工资管理系统运行过程中，需要及时地呈报报表，以便上级能及时了解工资管理系统运行状况，做出相应重要的决策。\\r\\n系统主要功能包括报表数据的导入、录入、修改、审核、批准、查询、组合查询、数据导入、备份，生成报表、查询报表、操作报表、打印、报表输出到Excel、Pdf、Html、Word等文件、报表导入、备份等；用户管理包括用户创建、用户删除、用户权限分配管理等。\\r\\n ',
                }
            ]
        },
        {
            xtype: 'panel',
           // height: 150,
            title: '通知通告',
            html: '在职人员___在职人员__在工资管理系统运行过程中，需要及时地呈报报表，以便上级能及时了解工资管理系统运行状况，做出相应重要的决策。\\r\\n系统主要功能包括报表数据的导入、录入、修改、审核、批准、查询、组合查询、数据导入、备份，生成报表、查询报表、操作报表、打印、报表输出到Excel、Pdf、Html、Word等文件、报表导入、备份等；用户管理包括用户创建、用户删除、用户权限分配管理等。\\r\\n ',
        },
        {
            xtype: 'panel',
           // height: 500,
            title: '图示',
            iconCls: 'fa fa-bus',
            layout: 'column',
            items: [
                {
                    xtype: 'polar',
                    header: false,
                    columnWidth: 0.3,
                    //colors: ['#fed581','#1aa51b','#ee929c','#9aa5db'],
                    reference: 'chart',
                    innerPadding: 40,
                    width: '100%',
                    height: 500,
                    store: 'PieChartStore',
                    theme: 'Muted',
                    interactions: ['itemhighlight', 'rotatePie3d'],
                    legend: {
                        docked: 'bottom'
                    },
                    series: [
                        {
                            type: 'pie',
                            angleField: 'data1',
                            donut: 30,
                            distortion: 0.6,
                            highlight: {
                                margin: 40
                            },
                            label: {
                                field: 'name'
                            },
                            tooltip: {
                                trackMouse: true,
                                renderer: 'onSeriesTooltipRender'
                            }
                        }
                    ]
                },
                {
                    xtype: 'polar',
                    columnWidth: 0.4,
                    header: false,
                    //colors: ['#fed581','#1aa51b','#ee929c','#9aa5db'],
                    reference: 'chart',
                    innerPadding: 40,
                    width: '100%',
                    height: 500,
                     store: {
                     fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
                     data: [
                     {name: '教授 '+(8/(8+17+25+8+7)*100).toFixed(1)+'%',   data1: 8, data2: 12, data3: 14, data4: 8,  data5: 13},
                     {name: '副教授 '+(17/(8+17+25+8+7)*100).toFixed(1)+'%',   data1: 17,  data2: 8,  data3: 16, data4: 10, data5: 3},
                     {name: '讲师 '+(25/(8+17+25+8+7)*100).toFixed(1)+'%', data1: 25,  data2: 2,  data3: 14, data4: 12, data5: 7},
                     {name: '教员 '+(8/(8+17+25+8+7)*100).toFixed(1)+'%',  data1: 8,  data2: 14, data3: 6,  data4: 1,  data5: 23},
                     {name: '实验员 '+(6/(8+17+25+8+7)*100).toFixed(1)+'%',  data1: 7, data2: 38, data3: 36, data4: 13, data5: 33}
                     ]
                     },
                    // store: 'PieChartStore',
                    // store: {type: 'device-market-share'},
                    //theme: 'Muted',
                    interactions: ['itemhighlight', 'rotatePie3d'],
                    legend: {
                        docked: 'bottom'
                    },
                    series: [
                        {
                            type: 'pie',
                            // type: 'pie3d',
                            angleField: 'data1',
                            donut: 30,
                            distortion: 0.6,
                            highlight: {
                                margin: 40
                            },
                            label: {
                                field: 'name'
                            },
                            tooltip: {
                                trackMouse: true,
                                renderer: 'onSeriesTooltipRender'
                            }
                        }
                    ]
                },
                {
                    xtype: 'polar',
                    columnWidth: 0.3,
                    header: false,
                    //colors: ['#fed581','#1aa51b','#ee929c','#9aa5db'],
                    reference: 'chart',
                    innerPadding: 40,
                    width: '100%',
                    height: 500,
                    /* store: {
                     fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
                     data: [
                     {name: '教授',   data1: 10, data2: 12, data3: 14, data4: 8,  data5: 13},
                     {name: '副教授',   data1: 7,  data2: 8,  data3: 16, data4: 10, data5: 3},
                     {name: '讲师', data1: 5,  data2: 2,  data3: 14, data4: 12, data5: 7},
                     {name: '教员',  data1: 2,  data2: 14, data3: 6,  data4: 1,  data5: 23},
                     {name: '实验员',  data1: 27, data2: 38, data3: 36, data4: 13, data5: 33}
                     ]
                     },*/
                    store: 'PieChartStore',
                    // store: {type: 'device-market-share'},
                    theme: 'Muted',
                    interactions: ['itemhighlight', 'rotatePie3d'],
                    legend: {
                        docked: 'bottom'
                    },
                    series: [
                        {
                            type: 'pie',
                            angleField: 'data1',
                            donut: 30,
                            distortion: 0.6,
                            highlight: {
                                margin: 40
                            },
                            label: {
                                field: 'name'
                            },
                            tooltip: {
                                trackMouse: true,
                                renderer: 'onSeriesTooltipRender'
                            }
                        }
                    ]
                }
            ]
        }
    ],
    initComponent: function () {
        this.callParent(arguments);
    }
}); """)
            FileWriter fw = new FileWriter("D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\view\\"+tablename+"\\"+initcap2(tablename)+"MainpageViewport.js");
            PrintWriter pw = new PrintWriter(fw);
            pw.println(sb);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    def  initcap2(String str) {//不带路径不带盘符的名字第一个字母改为大写
        int i=0;
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') ch[0] = (char) (ch[0]-32);
        return new String(ch);
    }
}
