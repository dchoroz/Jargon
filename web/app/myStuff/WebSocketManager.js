Ext.define('AM.myStuff.WebSocketManager', {
    requires:['AM.model.Message'],
    singleton: true,
    
    operations: null,
    
    asubSocket: null,
        
    constructor: function(config) {

        var me = this;
        config = config || {};

        var socket = $.atmosphere;
        var rMessage = Ext.create('AM.model.Message');

        this.operations = new Array();
        this.operations['read'] = new Array();
        this.operations['create'] = new Array();
        this.operations['update'] = new Array();
        this.operations['destroy'] = new Array();
        
        var request = {url: document.location.toString() + 'webresources/Connect',
            contentType: 'application/json',
            logLevel: 'debug',
            trackMessageLength : true,
            transport: 'websocket'
//            uuid: 0,
//            enableProtocol: true,
//            readResponseHeaders: false
//            fallbackTransport: 'long-polling'};
        };

        request.onOpen = function(response) {
            alert('a websocket opened with UUID: '+response.request.uuid+' '+response.request.trackMessageLength);//+AM.myStuff.WebSocketManager.asubSocket.uuid +'  '+ response.request.uuid);
            AM.myStuff.WebSocketManager.asubSocket.uuid = response.request.uuid;
        };

        request.onReconnect = function(response) {
            alert('websocket Reconnection');//+AM.myStuff.WebSocketManager.asubSocket.uuid +'  '+ response.request.uuid);
            AM.myStuff.WebSocketManager.asubSocket.uuid = response.request.uuid;
        };
        
        request.onReopen = function(response) {
            alert('websocket Reopen');//+AM.myStuff.WebSocketManager.asubSocket.uuid +'  '+ response.request.uuid);
            AM.myStuff.WebSocketManager.asubSocket.uuid = response.request.uuid;
        };
        
        request.onClientTimeout = function(response) {
            alert('websocket ClientTimeOut');//+AM.myStuff.WebSocketManager.asubSocket.uuid +'  '+ response.request.uuid);
            AM.myStuff.WebSocketManager.asubSocket.uuid = response.request.uuid;
        };
        
        request.onMessage = function(response) {
            var me = this;
            var message = response.responseBody;
            console.log('websocket message received: ' , message);
            var options = {};
            
            try {

            //Get message Reader and read the received message from the server
                var messageReader = rMessage.getProxy().getReader();
                response.responseText = response.responseBody;
                messageReader.applyDefaults = 'read';
                var json = messageReader.read(response);
            //end
                AM.myStuff.WebSocketManager.decodeMessage(response, json.records[0], response.request.uuid);

            } catch (e) {
                console.log('Error while commiting changes response: '+ e);
                return;
            }
        };

        request.onError = function(response) {
            alert('Sorry, but there is some problem with your '
                    + 'socket or the server is down');
        };

//        me.mixins.observable.constructor.call(me, config);
        me.asubSocket = socket.subscribe(request, console.log('a subsocket was created'));

    },
          
    decodeMessage: function(rawResponse, response, responseUuid) {

        var success = response.get('success');
        var time = response.get('time');
        var messageId = response.get('messageId');
        var senderUuid = response.get('uuid');
        var transaction = response.get('transaction');
        var target = response.get('target');
        
        var responseOperations = response.get('operations');
        
        console.log('WebSocketManager decodeMessage: responseOperations '+responseOperations.length);
        this.commitOperations(rawResponse, responseOperations, senderUuid);

    },
    
    commitOperations: function(response, responseOperations, responseUuid){
        
        /**Check if operation is on the clients array**/
        var operation = null;
                
        for(var i = 0; i < responseOperations.length; i++){
            alert(responseOperations.length);
            var action = responseOperations[i].action;
            var operationId = responseOperations[i].operationId;
            var type = responseOperations[i].type;
            
//            var data = new Array();
            var model = Ext.ModelManager.getModel('AM.model.'+type);
  
            var dataReader = model.getProxy().getReader()
            dataReader.applyDefaults = 'read';
            var json = dataReader.read(responseOperations[i].data);
            console.log(json);
 
 ////////////////this for is not needed because I use model's JsonReader to read data from the response
//            for(var j = 0; j<responseOperations[i].data.length; j++){
//                data[j] = Ext.create('AM.model.'+type, responseOperations[i].data[j]);
//            }
    
            if(AM.myStuff.WebSocketManager.asubSocket.uuid === responseUuid){
                console.log('ta uuids tairiazoun '+AM.myStuff.WebSocketManager.asubSocket.uuid+' '+responseUuid);
                operation = AM.myStuff.WebSocketManager.getOperation(action, operationId);
            }
        
//            alert("operation " + operation);
        
            if(operation !== null){
                Ext.apply(operation, {
                    response: response,
                    resultSet: new Ext.data.ResultSet({records: json.records})
                });
                                
                operation.commitRecords(json.records);
                operation.setCompleted();
                operation.setSuccessful();
                
                if (typeof operation.batchCallback == 'function') {
//                    console.log( operation.batchCallback +' '+operation.batchScope);
                    operation.batchCallback.call( operation.batchScope , operation);
                }
        
            }else{
               var stores = this.getStoresByModel(type);
                    if(action === 'create'){
                        //if a create operation is done by another client then 
                        //not all stores should get informed about the new item.
                        for(var j=0; j<stores.items.length; j++){
                            stores.get(j).loadData(json.records, true);
                        }  
                    }else if(action === 'destroy'){
                        for(var j=0; j<stores.items.length; j++){
                            var storeData = stores.get(j);
                            storeData.getProxy().updateStore(json.records, storeData, action);
                            
                        }
                    }else if(action === 'update'){
                        for(var j=0; j <stores.items.length; j++){
                            var storeData = stores.get(j);
                            storeData.getProxy().updateData(json.records, storeData, action);
                        }
                    }
                    
                    console.log('found '+stores.length+' stores for AM.model.'+type+' model.');

            }
            
        }
        console.log("received data commited");
    },
    
    send: function(json) {
    
        this.asubSocket.push(json);
        console.log('Message sent to server. UUID: '+this.asubSocket.getUUID());
       
    },
            
//    getJsonRecords: function(records) {
//        var json;
//        var properties;
//        for (var i = 0; i < records.length; i++) {
//            properties = Object.getOwnPropertyNames(records[i].data);
//            for (var j = 0; j < properties.length; j++) {
//
//            }
////            alert(properties);
////            json += properties;
//        }
//    },
//       
/*function retrieving operation from client's array
 * if not found null is returned*/            
    getOperation: function(action, receivedOperationId){
        var clientOperations = AM.myStuff.WebSocketManager.operations[action];
        //alert('PSAXNW STON PINAKA MOU TO OPERATION '+action+' '+receivedOperationId+' '+clientOperations.length);
        for(var i=0; i<clientOperations.length; i++){
              console.log('PSAXNW STON PINAKA MOU TO OPERATION '+action+' '+clientOperations[i].operationId+' '+receivedOperationId+' '+clientOperations.length);
            if(clientOperations[i].operationId === receivedOperationId) {
                var operation = AM.myStuff.WebSocketManager.operations[action].splice(i,1)[0];

                return operation;
            }
        }
            
        return null;
    },
            
    pushOperation: function(action, operation){
        console.log("pushOperation: "+operation.getRecords());
        this.operations[action].push(operation);
    },
    
    getStoresByModel: function(modelName){
        var storeManager = Ext.data.StoreManager,
            items = storeManager.items,
            length = storeManager.items.length,
            newMC  = new storeManager.self(),
            keys = storeManager.keys;
        
        for(var i=0; i<length; i++){
            if(items[i].model.modelName === 'AM.model.'+modelName){
                newMC.add(keys[i], items[i]);
            }
        }
        
        return newMC;
    }
    
});

