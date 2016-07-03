Ext.define('AM.view.user.PersonList' ,{
    extend: 'Ext.grid.Panel',
    alias: 'widget.userlist',
    
    title: 'All Users',

    store: 'AM.store.TransactionUser',
    
    initComponent: function() {
        this.buttons =[ 
            {text: 'Load All Users', action: 'loadallusers'},
            {text: 'Load Filtered Users', action: 'loadfilteredusers'},
            {text: 'Create New Account', action: 'create'},
            {text: 'Delete Account', action: 'delete'},
            {text: 'Sync Data', action: 'syncData'},
            {text: 'Get Associated', action: 'associated'},
            {text: 'Clear', action: 'clearData'},
            {text: 'TestCreate', action: 'testCreate'}
        ];
        this.columns = [
            {header: 'ID',  dataIndex: 'ic'},
            {header: 'First Name', dataIndex: 'fname', flex: 1},
            {header: 'Last Name', dataIndex: 'lname', flex: 1},
            {header: 'Age', dataIndex: 'age', flex: 1}
        ];
   		 
        this.callParent(arguments);
    },
            
     
    
});
