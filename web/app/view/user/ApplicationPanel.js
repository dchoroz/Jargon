Ext.define('AM.view.user.ApplicationPanel', {
    extend: 'Ext.Panel',
    alias: 'widget.masterView',
    frame: true,
    title: 'Users List',
    renderTo: Ext.getBody(),
       
        layout: 'anchor',

    // override initComponent
    initComponent: function () {
        this.items = [
            {
                xtype: 'userlist',
                itemId: 'personPanel',
                region: 'north',
                anchor: '100% 75%'
//                split: true
            }, 
            {   
                xtype: 'phonelist',
                itemId: 'phonePanel',
                region: 'south',
                anchor: '100% 25%'

            }
        ];

        // call the superclass's initComponent implementation
        this.callParent(arguments);
    },
    initEvents: function () {
        // call the superclass's initEvents implementation
        this.callParent();

        // now add application specific events
        // notice we use the selectionmodel's rowselect event rather
        // than a click event from the grid to provide key navigation
        // as well as mouse navigation
        var personGridSm = this.getComponent('personPanel').getSelectionModel();
        personGridSm.on('selectionchange', this.onRowSelect, this);
    },
    // add a method called onRowSelect
    // This matches the method signature as defined by the 'rowselect'
    // event defined in Ext.selection.RowModel
    onRowSelect: function (sm, rs) {
        // getComponent will retrieve itemId's or id's. Note that itemId's
        // are scoped locally to this instance of a component to avoid
        // conflicts with the ComponentManager
        if (rs.length) {
            var phoneList = this.getComponent('phonePanel');
            var templateData = [];
            var phoneData = rs[0].phoneNumbers().data;
//            for(var i=0; i<rs[0].getphoneNumbersList().getCount(); i++){                
//                templateData[i] = phoneData.getAt(i).data;
//            }
            phoneList.update(phoneData);
        }

    }
});
