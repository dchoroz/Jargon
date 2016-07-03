Ext.define('AM.myStuff.TransactionStore',{
   extend: 'Ext.data.Store',
   
   transactionCollection: undefined,
   //user defined function to call when data contained in this store are changed by someone else
   onDataChange: Ext.emptyFn,
   
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
   
//    mixins: {
//        observable: 'Ext.util.Observable',
//        sortable: 'Ext.util.Sortable'
//    },
   
   constructor: function(config){
        // Clone the config so we don't modify the original config object
        config = config || {};
        var me = this;

       //We want data to be sent to the server only when commitChanges is called
        config.autoSync = false;
        config.batchUpdateMode = 'complete';
       

        me.callParent([config]);
        
   },
           
   commitChanges: function(){
       var me = this;
       
       var options = me.getDirty();
             
       console.log('commitchanges');
       if(options !== undefined){
            var batch = me.proxy.batch(options);            
            me.proxy.sendTransactionalMessage();
       }else{
           console.log(me.storeId+" no changes to be done");
       }
   },
   
   clearTransaction: function(){
       this.removeAll();
   },
   
   /*this function can be used from onDataChange to update the data*/        
   reload: function(clientRec, serverRec, action){
        var me = this;
        var index = me.data.indexOf(clientRec);
        
        if(action == 'destroy'){

             clientRec.unjoin(me);
             me.data.remove(clientRec);

             me.fireEvent('remove', me, clientRec, index);

        }else if(action == 'destroy'){

            clientRec.copyFrom(serverRec);

            me.fireEvent('update', me, clientRec, index);
        }
        me.fireEvent('datachanged', me);
        me.fireEvent('refresh', me);
   },   
          
   /*rollback does not have a clear meaning on the client side. if a change on the data of the store occurs 
    * the user can keep the dirty data or commit the new data coming from the server*/
   rollback: function(callback){
       /**
        * Maybe a MixedCollection should be held to contain all the data before the changes start, 
        * but what is the purpose to return to a condition that is not represented by the data on the server
        * (because someone else changed them). 
        * Maybe here the store should replace all the data that have ids with the ones from the server
        * so a me.read(ids); should be called and the data that is phantom stay as they are or delete them.
        */
       
   },
   
   join: function(transCollection){
       this.transactionCollection = transCollection;
   },
   
   unjoin: function(){
       this.transactionCollection = undefined;
   },
   
   getDirty: function(options){
       var me = this,
            operations = {},
            toCreate = me.getNewRecords(),
            toUpdate = me.getUpdatedRecords(),
            toDestroy = me.getRemovedRecords(),
            needsSync = false;
    
        if (toCreate.length > 0) {
            operations.create = toCreate;
            needsSync = true;
        }

        if (toUpdate.length > 0) {
            operations.update = toUpdate;
            needsSync = true;
        }

        if (toDestroy.length > 0) {
            operations.destroy = toDestroy;
            needsSync = true;
        }

        if (needsSync && me.fireEvent('beforesync', operations) !== false) {
            options = options || {};
            
            Ext.apply(options, {
                operations: operations,
                listeners: me.getBatchListeners()
            });
        }
        
        return options;
   }
   
});