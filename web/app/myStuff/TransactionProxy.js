Ext.define('AM.myStuff.TransactionProxy', {
    extend: 'AM.myStuff.WebSocketManaged',
    alias: 'proxy.transactionProxy',
    
    isTransactional: true,
    operations: null,

    constructor: function(config) {

        var me = this;
        config = config || {};
        me.operations = new Array();
        
        me.callParent([config]);
    },
            
    create: function(operation, callback, scope){
        operation.batchCallback = callback;
        operation.batchScope = scope;
        this.addOperation(operation);
        callback.call( scope , operation);
    },
    
    read: function(operation, callback, scope){
        this.callParent(arguments);
    },
            
    update: function(operation, callback, scope){
        operation.batchCallback = callback;
        operation.batchScope = scope;
        this.addOperation(operation);
        callback.call( scope , operation);

    },

    destroy: function(operation, callback, scope){
        operation.batchCallback = callback;
        operation.batchScope = scope;
        this.addOperation(operation);        
        callback.call( scope , operation);
    },
            
    updateStore: function(data, store, action){
        var me = this;
        var storeData = store.data;
        console.log(data);
        
        for(var i=0; i< data.length; i++){
            var clientRec = storeData.findBy(me.matchClientRec, data[i]);
            console.log(clientRec);

            if(clientRec !== null){
                if(Ext.isFunction(store.onDataChange)){
                    store.onDataChange(clientRec, data[i], action);
                }else{
                    store.reload(clientRec, data[i], action);
                }
            }
        }
    },
            
    sendTransactionalMessage: function(){
        var me = this;
        var crudOperations = new Array();
        
        for(var i=0; i<this.operations.length; i++){
            AM.myStuff.WebSocketManager.pushOperation(me.operations[i].action, me.operations[i]);
            crudOperations.push(me.getCrudOperation(me.operations[i]).data);
        }
        
        var newMessage = Ext.create('AM.model.Message', {
            success: true,
            time: +new Date(),
            messageId: this.getUniqueId(),
            transaction: me.isTransactional || false,            
            operations: crudOperations
        });
        
        var json = Ext.JSON.encode(newMessage.data);
        this.operations = [];
        AM.myStuff.WebSocketManager.send(json);
    },
            
    addOperation: function(operation){
        this.operations.push(operation);
    },
})