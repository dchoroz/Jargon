Ext.define('AM.view.user.Edit', {
    extend: 'Ext.window.Window',
    alias: 'widget.useredit',

    title: 'Edit User',
    layout: 'fit',
    autoShow: true,

    initComponent: function() {
        this.items = [
            {
                xtype: 'form',
                items: [
                    {
                        xtype: 'textfield',
                        name : 'fname',
                        fieldLabel: 'First Name'
                    },
                    {
                    	xtype: 'textfield',
                    	name: 'lname',
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
                text: 'Save',
                action: 'save'
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
