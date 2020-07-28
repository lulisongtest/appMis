//不用了
Ext.define('appMis.view.student.StudentMainpageViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.studentmainpageviewport',
    glyph: 'xf015@FontAwesome',
    closable: true,
    icon: 'images/application/application_form.png',
    title: '首页',
    requires: [
        'appMis.view.charts.column.Stacked',
        'appMis.view.charts.area.Stacked',
        //'appMis.view.charts.column3d.Grouped'
        'appMis.view.charts.Dwyear'
    ],
    items: [
        {
            xtype: 'panel',
           // height: 500,
            title: '图示',
            header: false,
            iconCls: 'fa fa-bus',
            layout: 'column',
            items: [
                {
                    xtype: 'polar',
                    columnWidth: 1,                    // theme: 'green',
                   // interactions: 'rotate',
                    header: false, //colors: ['#fed581','#1aa51b','#ee929c','#9aa5db'],                    //reference: 'chart',
                    innerPadding: 25,
                    width: '85%',
                    height: 350,
                    store: 'StudentPieChartStore',
                    theme: 'Muted',
                    interactions: ['itemhighlight', 'rotatePie3d'],
                    legend: {
                        docked: 'bottom'
                    },
                    sprites: [{
                        type: 'text',
                        text: '公务员类',
                        textAlign: 'center',
                        fontSize: 14,
                        fontWeight: 'bold',
                       // width: '33%',
                        height: 60,
                        x: 50, // the sprite x position
                        y: 190  // the sprite y position
                    }],
                    series: {
                        type: 'pie3d',  //type: 'pie',
                        angleField: 'data1',
                        donut: 40,
                        distortion: 0.45,
                        highlight: {
                            margin: 20
                        },
                        label: {
                            field: 'name'

                        },
                        tooltip: {
                            trackMouse: true,
                            renderer: 'onSeriesTooltipRender'
                        }
                    }
                },
                {
                    xtype: 'polar',
                   //columnWidth: 0.33,
                    columnWidth: 1,
                    header: false,                    //colors: ['#fed581','#1aa51b','#ee929c','#9aa5db'],
                    reference: 'chart',
                    innerPadding: 25,
                    width: '100%',
                    height: 350,
                     store: {
                     fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
                     data: [
                         {name: '正高 '+(8/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%',   data1: 8, data2: 12, data3: 14, data4: 8,  data5: 13},
                         {name: '副高 '+(17/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%',   data1: 17,  data2: 8,  data3: 16, data4: 10, data5: 3},
                         {name: '中级 '+(25/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%', data1: 25,  data2: 2,  data3: 14, data4: 12, data5: 7},
                         {name: '助级 '+(8/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%',  data1: 8,  data2: 14, data3: 6,  data4: 1,  data5: 23},
                         {name: '员级 '+(6/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%',  data1: 7, data2: 38, data3: 36, data4: 13, data5: 33},
                        // {name: '正局 '+(8/(8+17+25+8+7+8+17+25+8+25+8+25+8+8)*100).toFixed(1)+'%',   data1: 8, data2: 12, data3: 14, data4: 8,  data5: 13},
                        // {name: '副局 '+(17/(8+17+25+8+7+8+17+25+8+25+8+25+8+8)*100).toFixed(1)+'%',   data1: 17,  data2: 8,  data3: 16, data4: 10, data5: 3},
                         {name: '正处 '+(25/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%', data1: 25,  data2: 2,  data3: 14, data4: 12, data5: 7},
                         {name: '副处 '+(8/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%',  data1: 8,  data2: 14, data3: 6,  data4: 1,  data5: 23},
                         {name: '正科 '+(25/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%', data1: 25,  data2: 2,  data3: 14, data4: 12, data5: 7},
                         {name: '副科 '+(8/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%',  data1: 8,  data2: 14, data3: 6,  data4: 1,  data5: 23},
                         {name: '科员 '+(25/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%', data1: 25,  data2: 2,  data3: 14, data4: 12, data5: 7},
                         {name: '办事员 '+(8/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%',  data1: 8,  data2: 14, data3: 6,  data4: 1,  data5: 23},
                         {name: '工人 '+(8/(8+17+25+8+7+25+8+25+8+25+8+8)*100).toFixed(1)+'%',  data1: 8,  data2: 14, data3: 6,  data4: 1,  data5: 23}
                     ]
                     },
                    // store: 'PieChartStore',
                    // store: {type: 'device-market-share'},
                    //theme: 'Muted',
                    interactions: ['itemhighlight', 'rotatePie3d'],
                    legend: {
                        docked: 'bottom'
                    },
                    sprites: [{
                        type: 'text',
                        text: '事业编类',
                        textAlign: 'center',
                        fontSize: 14,
                        fontWeight: 'bold',
                        //width: '33%',
                        height: 60,
                        x: 50, // the sprite x position
                        y: 190  // the sprite y position
                    }],
                    series: [
                        {
                            type: 'pie3d',// type: 'pie',
                            angleField: 'data1',
                            donut: 35,
                            distortion: 0.45,
                            highlight: {
                                margin: 20
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
                /*,
                {
                    xtype: 'polar',
                    columnWidth: 1,
                    header: false,                    //colors: ['#fed581','#1aa51b','#ee929c','#9aa5db'],
                    reference: 'chart',
                    innerPadding: 25,
                    width: '85%',
                    height: 350,
                    store: 'StudentPieChartStore',                    // store: {type: 'device-market-share'},
                    theme: 'Muted',
                    interactions: ['itemhighlight', 'rotatePie3d'],
                    legend: {
                        docked: 'bottom'
                    },
                    sprites: [{
                        type: 'text',
                        text: '石油企业工资类',
                        textAlign: 'center',
                        fontSize: 14,
                        fontWeight: 'bold',
                        //width: '33%',
                        height: 60,
                        x: 50, // the sprite x position
                        y: 190  // the sprite y position
                    }],
                    series: [
                        {
                            type: 'pie3d',// type: 'pie',
                            angleField: 'data1',
                            donut: 35,
                            distortion: 0.45,
                            highlight: {
                                margin: 20
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
                    xtype: 'panel',
                    columnWidth: 1,
                    width: '100%',
                    height: 1500,
                    items:[{
                        xtype: 'dw-year-3d'
                    }]
                }*/
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
    }
});