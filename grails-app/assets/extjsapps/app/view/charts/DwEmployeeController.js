Ext.define('salaryMis.view.charts.DwEmployeeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dw-employee-3d',

    onPreview: function() {
        var chart = this.lookupReference('chart2');
        chart.preview();
    },

    onThemeSwitch: function () {
        var chart = this.lookupReference('chart2'),
            currentThemeClass = Ext.getClassName(chart.getTheme()),
            themes = Ext.chart.theme,
            themeNames = [],
            currentIndex = 0,
            name;

        for (name in themes) {
            if (Ext.getClassName(themes[name]) === currentThemeClass) {
                currentIndex = themeNames.length;
            }
            if (name !== 'Base' && name.indexOf('Gradients') < 0) {
                themeNames.push(name);
            }
        }
        chart.setTheme(themes[themeNames[++currentIndex % themeNames.length]]);
    },

    onStackGroupToggle: function (segmentedButton, button, pressed) {
        var chart = this.lookupReference('chart1'),
            series = chart.getSeries()[0],
            value = segmentedButton.getValue();

        series.setStacked(value === 0);
        chart.redraw();
    },


    onAxisLabelRender: function (axis, label, layoutContext) {
        // Custom renderer overrides the native axis label renderer.
        // Since we don't want to do anything fancy with the value
        // ourselves except adding a thousands separator, but at the same time
        // don't want to loose the formatting done by the native renderer,
        // we let the native renderer process the value first.
        var value = layoutContext.renderer(label) / 1;
        return value === 0 ? '0' : Ext.util.Format.number(value, '0');
    },

    onSeriesLabelRender: function (value) {
        return Ext.util.Format.number(value / 1, '0人');
    },

    onGridColumnRender: function (v) {
        return Ext.util.Format.number(v, '0,000');

    },
    onSeriesTooltipRender: function(tooltip, record, item) {
        var formatString = '0,000';
        tooltip.setHtml(record.get('department') + ' 在职人数：'+ Ext.util.Format.number(record.get(item.field).toFixed(1), formatString)+'人');
    }

});