Ext.define('AM.myStuff.TransactionCollection',{
    
    requires: [
        'Ext.util.MixedCollection',
    ], 
    
    collectionId: undefined,
    stores: undefined,
     
    statics: {
        recordIdFn: function(record) {
            return record.storeId;
        },
        recordIndexFn: function(record) {
            return record.index;
        }
    },

    proxy: {
        type: 'transactionProxy',
        reader: {
            type: 'json',
            root: 'data',
        },
        writer: {
            type: 'bjson',
            root: 'data',
        }
    },   
            
    constructor: function(config){
        config = Ext.Object.merge({}, config);
        
        var me = this,
                stores;
        
        stores = config.stores || me.stores;
        me.stores = new Ext.util.MixedCollection(false, AM.myStuff.TransactionCollection.recordIdFn);
        me.collectionId = config.collectionId || Ext.id(null, 'transCol-');
        
        if(stores){
            me.addStore.apply(me, [stores]);
        }

        var proxy = Ext.createByAlias('proxy.' + me.proxy.type, me.proxy);
        me.proxy = proxy;

    },
    
    addStore: function(storeId){
        var store = Ext.data.StoreManager.lookup(storeId);
        
        if(store){
            this.stores.add(storeId, store);
            store.join(this.collectionId);
        }else{
            console.log(storeId+': No store with this id is registered to the StoreManager.');
        }
    },
            
    removeStore: function(storeId){
        var store = Ext.data.StoreManager.lookup(storeId);
        
        this.stores.removeAtKey(storeId);
        store.unjoin();
    },
    
    getStores: function(){
        
        var data = [],
            stores = this.stores;
        
        
        for(var i = 0; i<this.stores.getCount(); i++ ){
            data.concat(stores.getAt(i).storeId);
        }
        
        return data;
    },
    
    getStoreById: function(storeId){
        return this.stores.getByKey(storeId);
    },
    
    commitChanges: function(){
        var me = this,
            i,
            stores = me.stores,
            options=[];
    
    
//        options.operations = [];
//        options.listeners = [];
        
        for(var i = 0; i < stores.getCount(); i++){
            var store = stores.getAt(i);
            var storeOptions = store.getDirty();
            options.push(storeOptions);

//            console.log(storeOptions.operations);
//            options.operations.push( storeOptions.operations);
//            options.listeners.push( storeOptions.listeners);
//            console.log(options);
        }
        
        for(var i = 0; i < stores.getCount(); i++){
//            var storeOptions = stores.getAt(i).getDirty();
            var batch = me.proxy.batch(options[i]);            
            

//            console.log(storeOptions.operations);
//            options.operations.push( storeOptions.operations);
//            options.listeners.push( storeOptions.listeners);
//            console.log(options);
        }
       me.proxy.sendTransactionalMessage(); 
//        if(options.length > 0){
//            var batch = me.proxy.batch(options);            
//            me.proxy.sendTransactionalMessage();
//       }else{
//           console.log(me.collectionId+" no changes to be done");
//       }
    },
            
    clearTransaction: function(){
        var stores = this.stores;
        
        stores.each(function(item, index, length){
            item.clearTransaction();
        });
        
//        for(var i = 0; i<this.stores.getCount(); i++ ){
//            stores.getAt(i).clearTransaction();
//        }
    },
     
    rollback: function(){
        var stores = this.stores;
        
        stores.each(function(item, index, length){
            item.rollback();
        });
    }
});