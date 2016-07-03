Ext.define('AM.myStuff.WebSocketManaged', {
    extend: 'Ext.data.proxy.Proxy',
    alias: 'proxy.websocketmanaged',
    
    
//    requires:['AM.myStuff.webSocketManager'],

//    mixins: {
//        observable: 'Ext.util.Observable'
//    },
    
    constructor: function(config) {

        var me = this;
        config = config || {};
        
        //config.batchActions = false;
        me.callParent([config]);
    },
            
//    getReader: function() {
//        return this.reader;
//    },
            
    create: function(operation, callback, scope) {
        var me = this;
        
        var crudOperation = me.getCrudOperation(operation);
       
        operation.batchCallback = callback;
        operation.batchScope = scope;       
        
        var crudOperationJson = Ext.JSON.encode(crudOperation.data);
     
        var newMessage = Ext.create('AM.model.Message', {
            success: true,
            time: +new Date(),
            messageId: this.getUniqueId(),
            transaction: me.isTransactional || false,            
            operations: [crudOperation.data]
        });


        AM.myStuff.webSocketManager.pushOperation('create', operation);

        var json = Ext.JSON.encode(newMessage.data);
        //        alert(json);
        AM.myStuff.webSocketManager.send(json);
        

//        if (typeof callback == 'function') {
//            callback.call(scope || me, operation);
//        }
    },
            
    read: function(operation, callback, scope) {
        var me = this;

        var classType = me.getSimpleName(me.getModel().getName());
        operation.operationId = Ext.id(null, 'Operation-');
      
//        alert("Read operation: "+ operation.filters.length);

        var filters = null;
            
        if(Ext.isDefined(operation.filters)){
//            alert('WebSocketManaged read, filters' + operation.filters );
            filters = me.encodeFilters(operation.filters);
        }
        for(var i = 0; i< filters.length; i++){
//            alert(filters[i].value);
        }
        var crudOperation = Ext.create('AM.model.CrudOperation',{
            action: operation.action,
            operationId: operation.operationId,
            type: classType,
            target: "basic.SimpleCrud.crudData",
            data: filters
        });

//        var crudOperationJson = Ext.JSON.encode(crudOperation.data);

        var newMessage = Ext.create('AM.model.Message', {
            success: true,
            time: +new Date(),
            messageId: this.getUniqueId(),
            transaction: false,
            operations: [crudOperation.data]
        });

        operation.batchCallback = callback;
        operation.batchScope = scope;
        AM.myStuff.WebSocketManager.pushOperation('read', operation);

//        console.log(newMessage.get("action") +" "+newMessage.get("timestamp")+" "+newMessage.get("list"));
        var json = Ext.JSON.encode(newMessage.data);
//        alert('WebsocketManaged.read: message created: '+json +" "+operation.success);

        AM.myStuff.WebSocketManager.send(json);
        
//        if (typeof callback == 'function') {
//            callback.call(scope || me, operation);
//        }
    },
            
    update: function(operation, callback, scope) {
//        return this.doRequest.apply(this, arguments);
        var me = this;
        
        var crudOperation = me.getCrudOperation(operation);
        var crudOperationJson = Ext.JSON.encode(crudOperation.data);

        var newMessage = Ext.create('AM.model.Message', {
            success: true,
            time: +new Date(),
            messageId: this.getUniqueId(),
            transaction: me.isTransactional || false,            
            operations: [crudOperation.data]
        });
        
        operation.batchCallback = callback;
        operation.batchScope = scope;
        AM.myStuff.WebSocketManager.pushOperation('update', operation);

//        console.log(newMessage.get("action") +" "+newMessage.get("timestamp")+" "+newMessage.get("list"));
        var json = Ext.JSON.encode(newMessage.data);
//        alert(json);

        AM.myStuff.WebSocketManager.send(json);

//        if (typeof callback == 'function') {
//            callback.call(scope || me, operation);
//        }
    },
            
    destroy: function(operation, callback, scope) {
        var me = this;
        var records = operation.getRecords();
        operation.operationId = Ext.id();

        var crudOperation = me.getCrudOperation(operation);
        var crudOperationJson = Ext.JSON.encode(crudOperation.data);

        var newMessage = Ext.create('AM.model.Message', {
            success: true,
            time: +new Date(),
            messageId: this.getUniqueId(),
            transaction: me.isTransactional || false,             
            operations: [crudOperation.data]
        });
        
        operation.batchCallback = callback;
        operation.batchScope = scope;
        AM.myStuff.WebSocketManager.pushOperation('destroy', operation);

//        console.log(newMessage.get("action") +" "+newMessage.get("timestamp")+" "+newMessage.get("list"));
        var json = Ext.JSON.encode(newMessage.data);
//        alert(json);

        AM.myStuff.WebSocketManager.send(json);
        
//        if (typeof callback == 'function') {
//            callback.call(scope || me, operation);
//        }
    },
        
    updateStore: function(data, store, action){
        if(action == 'destroy'){
            this.removeData(data, store);
        }else if(action == 'update'){
            this.updateData(data, store);
        }
    },
            
    removeData: function(data, store) {
        var me = this;
        var storeData = store.data;
        
        for(var i=0; i< data.length; i++){
            var clientRec = storeData.findBy(me.matchClientRec, data[i]);

            if(clientRec !== null){
                if(this.isTransactional){
                    if(Ext.isFunction(store.onDataChange)){
                        store.onDataChange(clientRec, 'remove');
                    }
                }
                
                

            }
        }
    },
    
    updateData: function(data, store){
        var me = this;
        var storeData = store.data;
        
        for(var i=0; i< data.length; i++){
            var clientRec = storeData.findBy(me.matchClientRec, data[i]);
            if(clientRec !== null){
                if(this.isTransactional){
                    if(Ext.isFunction(store.onDataChange)){
                        store.onDataChange(clientRec, 'remove');
                    }
                }
                
               
               if(Ext.isFunction(store.onDataChange)){
                    store.onDataChange(clientRec, 'update');
               }
            }
        }
    },
            
    getJsonRecords: function(operation, records) {
        var data = [],
        record = {},
        writer = this.getWriter();
        
        for (var i = 0; i < records.length; i++) {

            data = data.concat(writer.getRecordData(records[i], operation));
        }
        
        return data;
    },

    getCrudOperation: function(operation){
        
        var me = this;
        operation.operationId = Ext.id(null, 'Operation-');
        
        var records = operation.getRecords();

        var data = me.getJsonRecords(operation, records);
        
//        alert("Create operation: "+operation.getRecords().length);
        var classType;
        
        if(records.isModel){
            classType = me.getSimpleName(records.modelName);
        }else{
            classType = me.getSimpleName(records[0].modelName);
        }
        
        return  Ext.create('AM.model.CrudOperation',{
            action: operation.action,
            operationId: operation.operationId,
            type: classType,
            target: "basic.SimpleCrud.crudData",
            data: data
        });
        
    },

    getSimpleName: function(name) {
        var lastDot = name.lastIndexOf('.');
        if (lastDot >= 0) {
            var name = name.substring(lastDot + 1);
        }

        return name;
    },
            
    matchClientRec: function(record) {
        var clientRec = this,
            clientRecordId = clientRec.getId();

        if(clientRecordId && record.getId() === clientRecordId) {
            return true;
        }
        // if the server record cannot be found by id, find by internalId.
        // this allows client records that did not previously exist on the server
        // to be updated with the correct server id and data.
        return record.internalId === clientRec.internalId;
    },
    
    encodeFilters: function(filters) {
        var min = [],
            length = filters.length,
            i = 0;

        for (; i < length; i++) {
            if(filters[i].value instanceof RegExp){
               filters[i].value = this.RegExptoString(filters[i].value);
            }
            min[i] = {
                property: filters[i].property,
                value   : filters[i].value
            };
        }
        return min;
    },
            
    applyEncoding: function(value) {
        return Ext.encode(value);
    },
            
    RegExptoString: function(value){
        value = value.toString();
        return value.substring(1,value.length-1);
    },

    getUniqueId: function() {

        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
                    return v.toString(16);
        });
    }
});