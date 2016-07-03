Ext.define('AM.store.TransactionUser',{
    extend: 'AM.myStuff.TransactionStore',
    model: 'AM.model.extra-Person',
    
    onDataChange: function(clientRec, serverRec, action){
        alert('onDataChange '+action+" "+clientRec+" "+serverRec);
        this.reload(clientRec, serverRec, action);
    },
                proxy: {
        type:'transactionProxy',
        reader: {
                type: 'json',
                root: 'data',
        },
        writer: {
                type: 'bjson',
                root: 'data',
        }
    }
    
    
});


