Ext.define('AM.view.user.Create', {
    extend: 'Ext.window.Window',
    alias: 'widget.usercreate',

    title: 'Create New Account',
    layout: 'fit',
    autoShow: true,

    initComponent: function() {
        this.items = [
            {
                xtype: 'form',
                items: [
                    {
                        xtype: 'textfield',
                        name : 'firstName',
                        fieldLabel: 'First Name'
                    },
                    {
                    	xtype: 'textfield',
                    	name: 'lastName',
                    	fieldLabel: 'Last Name'
                    },
                    {
                    	xtype: 'textfield',
                    	name: 'age',
                    	fieldLabel: 'Age'
                    }
                ]
            }
        ];

        this.buttons = [
            {
                text: 'Create',
                action: 'create'
            },
            {
                text: 'Cancel',
                scope: this,
                handler: this.close
            }
        ];

        this.callParent(arguments);
    }
});
