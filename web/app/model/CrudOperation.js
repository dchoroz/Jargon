Ext.define('AM.model.CrudOperation', {
    extend: 'Ext.data.Model',
   
    fields: [{name: 'action', type: 'String'},
            {name: 'operationId', type: 'String'},
            {name: 'type', type: 'String'},
            {name: 'target', type: 'string'},
            {name: 'data'}],
        
    proxy: {
        type: 'sessionstorage',
        id: 'operations',

        reader: {
            type: 'json',
            root: 'operations',
        },
        writer: {
            type: 'json'
        }
    }
});

