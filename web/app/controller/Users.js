Ext.define('AM.controller.Users', {
    extend: 'Ext.app.Controller',

    stores: ['AM.store.TransactionUser','AM.store.TransactionPhone'],
    models: ['AM.model.extra-Person','AM.store.extra-Phone'],
    
    views: [
        'user.ApplicationPanel',
        'user.PersonList',
        'user.PhoneList',
        'user.Edit',
        'user.Create',
        'user.CreatePhone'
    ],
    
    init: function() {
        this.control({
            'userlist': {
                itemdblclick: this.editUser,
//                itemclick: this.loadUserPhones
            },
            'phonelist button[action=addPhone]': {
                click: this.addPhone
            },
            'userlist button[action=loadallusers]': {
                click: this.loadallUsers
            },
            'userlist button[action=associated]': {
                click: this.getAssociated
            },
            'userlist button[action=loadfilteredusers]': {
                click: this.loadfilteredUsers
            },
            'userlist button[action=create]': {
                click: this.createUser
            },
            'userlist button[action=delete]': {
                click: this.deleteUser
            },
            'userlist button[action=syncData]': {
                click: this.syncData
            },
            'userlist button[action=clearData]': {
                click: this.clearStore
            },
            'useredit button[action=save]': {
                click: this.updateUser
            },
            'usercreate button[action=create]': {
                click: this.saveNewUser
            },
            'phonecreate button[action=create]':{
                click: this.addNewPhone
            },
            'userlist button[action=testCreate]':{
                click: this.testCreate
            }
        });
    },
        
    testCreate: function(button){
        var i;

        var storeCollection = Ext.create('AM.myStuff.TransactionCollection', {
            collectionId: 'takis',            
        });
        
        storeCollection.addStore('AM.store.TransactionUser');
        storeCollection.addStore('AM.store.TransactionPhone');
        
        var u1 = Ext.data.StoreManager.lookup('AM.store.TransactionUser');
        var u2 = Ext.data.StoreManager.lookup('AM.store.TransactionPhone');
        
            var newUser1 = Ext.create('AM.model.extra-Person', {
                fname: 'lakis',
                lname: 'pakis',
                age: 10
            });
            var newUser2 = Ext.create('AM.model.extra-Person', {
                fname: 'lakiss',
                lname: 'pakiss',
                age: 20
            });
            var newPhone = Ext.create('AM.model.extra-Phone', {
                number: 2109954394});
            
        u1.add(newUser1);
        u1.add(newUser2);

        u2.add(newPhone);

        storeCollection.commitChanges();

//        for(i=0; i<1000; i++){
//            var newUser = Ext.create('AM.model.extra-Person', {
//                fname: 'userfirst'+i,
//                lname: 'userlast'+i,
//                age: 10+i
//            });
////        newUser.phantom = true;    
//        var u = Ext.data.StoreManager.lookup('AM.store.TransactionUser');
////        newUser.getphoneNumbersList().add(Ext.create('AM.model.extra-Phone', {
////                number: '12938123812389'}));
//        u.add(newUser);
//        u.sync();
//        alert("go");
//        }
    },        
            
    getAssociated: function(button){
        var win = button.up('grid'),
        record = win.getSelectionModel().getSelection()[0];    

        var newPhone1 = Ext.create('AM.model.extra-Phone', {
                number: 2109954394});
                    var newPhone2 = Ext.create('AM.model.extra-Phone', {
                number: 2109954394});
                    var newPhone3 = Ext.create('AM.model.extra-Phone', {
                number: 2109954394});
//             var newPhone2 = Ext.create('AM.model.extra-Phone', {
//                number: 2109954394});
//             var newPhone3 = Ext.create('AM.model.extra-Phone', {
//                number: 2109954394});
//             var newPhone4 = Ext.create('AM.model.extra-Phone', {
//                number: 2109954394});
        record.phoneNumbers().add(newPhone1);
                        record.phoneNumbers().add(newPhone3);
        record.phoneNumbers().add(newPhone2);


//                record.getphoneNumbersList().add(newPhone3);
//        record.getphoneNumbersList().add(newPhone2);
//        record.getphoneNumbersList().add(newPhone4);
    //set record.writeStructuredData to true so the writer forwards the new associated phone
    
        //if the record has not been synced yet then a sync operation is needed to update the id on the view
        //otherwise an update action is triggered through record.save()
        if(record.phantom){
            this.syncData();
        }else{
            record.save();
        }
        
//        record.save();
//        record.getphoneNumbersList().sync();
        console.log(record.phoneNumbers().data.length);
    },
      
    loadallUsers: function(grid, record) {
        var u = Ext.data.StoreManager.lookup('AM.store.TransactionUser');
        u.clearFilter(true);
        
        u.load();
    },
            
    loadfilteredUsers: function(grid, record) {
        var u = Ext.data.StoreManager.lookup('AM.store.TransactionUser');
        u.clearFilter(true);
//        u.filter('age', 45);
//        u.filter('fname', 'lakis');
        u.filter('lname', 'dfjdf');

        u.load();
    },      
            
    editUser: function(grid, record) {
        var view = Ext.widget('useredit');
        view.down('form').loadRecord(record);
    },
            
    createUser: function(button) {

        var view = Ext.widget('usercreate');
    },
            
    deleteUser: function(button) {
        var win = button.up('grid'),
        record = win.getSelectionModel().getSelection()[0];
        
        var u = Ext.data.StoreManager.lookup('AM.store.TransactionUser');
        
        u.remove(record);
//        u.sync();
    },

    updateUser: function(button) {
        var win = button.up('window'),
                form = win.down('form'),
                record = form.getRecord(),
                values = form.getValues();

        //ManagePerson.updatePerson(record.get('id'), values.firstName, values.lastName);

        win.close();
        
        record.set('fname', values.fname);
        record.set('lname', values.lname);
        record.set('age', values.age);
//        record.setDirty();  
        
//    	if(record.dirty){
//    		record.commit();
//    	}

//        this.getUsersStore().sync();

    },
    saveNewUser: function(button) {
        var win = button.up('window'),
                form = win.down('form'),
                values = form.getValues();

        var newUser = Ext.create('AM.model.extra-Person', {
                fname: values.firstName,
                lname: values.lastName,
                age: values.age
            });
//        newUser.phantom = true;    
        var u = Ext.data.StoreManager.lookup('AM.store.TransactionUser');
//        newUser.getphoneNumbersList().add(Ext.create('AM.model.extra-Phone', {
//                number: '12938123812389'}));
        u.add(newUser);
//        console.log(newUser);
//        u.sync();
//        u.insert(0, newUser);
//        var message = JSON.stringify(newUser);
//        asubSocket.push(message);
//	ManagePerson.createPerson(values.firstName, values.lastName);
        win.close();
    },
    
    addPhone: function(){
        var view = Ext.widget('phonecreate');      
    },
            
    addNewPhone: function(button){
        var win = button.up('window'),
                form = win.down('form'),
                values = form.getValues();
        
        var newPhone = Ext.create('AM.model.extra-Phone', {
                number: values.number});
            
        var u = Ext.data.StoreManager.lookup('AM.store.extra-Phone');
        u.add(newPhone);
        
        win.close();
    },
    
//    loadUserPhones: function(grid, record){
//        var view = Ext.widget('phonelist');
//        
////        view.getSelectionModel().selectRange(8,9);
////        console.log('Data'+record.getphoneNumbersList().data+' '+view.getStore().getCount());
//        var store = view.getStore();
//        view.store = record.getphoneNumbersList();
//        console.log(record.getphoneNumbersList().getCount());
//        store.loadRecords(record.getphoneNumbersList().data, false);
//    },
    
    
    clearStore: function(){
        var u1 = Ext.data.StoreManager.lookup('AM.store.TransactionUser');
        u1.removeAll();
        alert('datacleared');
    },
    
    syncData: function(){
        var u1 = Ext.data.StoreManager.lookup('AM.store.TransactionUser');
        var u2 = Ext.data.StoreManager.lookup('AM.store.TransactionPhone');
//        console.log(u1.getProxy());
//        var transCol = Ext.create('AM.myStuff.TransactionCollecion');
//        transCol.addStore(u1);
//        transCol.addStore(u2);
 
//        u1.sync();
        u1.commitChanges();
    }

});
