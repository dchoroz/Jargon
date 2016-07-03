Ext.define('AM.model.Message', {
   extend: 'Ext.data.Model',
   fields: [{name: 'success', type: 'boolean'},
            {name: 'time', type: 'auto'},
            {name: 'messageId', type: 'String'},
            {name: 'transaction', type: 'boolean'},
            {name: 'uuid', type: 'String'},
            {name: 'operations', type: 'AM.Model.CrudOperations'}],
    proxy: {
        type: 'ajax',
        reader: {
            type: 'json'
        },
        writer: {
            type: 'json'
        }
    }
        
});

