Ext.define('AM.view.user.PhoneList' ,{
    extend: 'Ext.Panel',
    alias: 'widget.phonelist',
    
    title: 'Phone Numbers',

    tplMarkup: [
            ' <tpl for=".">{#} - id:{data.id}, number: {data.number}, person: {data.person}<br/></tpl>'
        ],
    startingMarkup: 'Please select person to see his phonenumbers',
    bodyPadding: 7,
    
    initComponent: function() {
        this.tpl = Ext.create('Ext.XTemplate', this.tplMarkup);
        this.html = this.startingMarkup;
        this.buttons =[ 
            {text: 'Add phone', action: 'addPhone'}
        ];

        this.bodyStyle = {
                background: '#ffffff'
            };
//        this.items = [
//            {
//                xtype: 'form',
//                items: [
//                    {
//                        xtype: 'textfield',
//                        name : 'phone',
//                        fieldLabel: 'Add Phone to User'
//                    }
//                ]
//            }
//        ];
        this.callParent(arguments);
    }
});
