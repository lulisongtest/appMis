/**
 * @class Ext.ux.upload.Button
 * @extends Ext.button.Button
 * 
 * @author Harald Hanek (c) 2011-2012
 * @license http://harrydeluxe.mit-license.org
 */
Ext.define('appMis.view.student.Upload', {
    extend: 'Ext.button.Button',
    alias: 'widget.uploadbutton',
    requires: ['appMis.view.student.UploadBasic'],
    disabled: true,

    listeners: {
        boxready: function () {
            var me = this;
            Ext.applyIf(me.config.uploader, {
                browse_button: me.config.id || me.getId()
            });

            me.uploader = me.createUploader();
            me.uploader.initialize();

            me.relayEvents(me.uploader, ['beforestart',
                'uploadready',
                'uploadstarted',
                'uploadcomplete',
                'uploaderror',
                'filesadded',
                'beforeupload',
                'fileuploaded',
                'updateprogress',
                'uploadprogress',
                'storeempty'
            ]);
        }
    },

    /**
     * @private
     */
    createUploader: function () {
        return Ext.create('appMis.view.student.UploadBasic', this, Ext.applyIf({
            listeners: {}
        }, this.initialConfig));
    }
});