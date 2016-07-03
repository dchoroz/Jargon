Ext.define('AM.myStuff.JsonWriter', {
    extend: 'Ext.data.writer.Json',
    alias: 'writer.bjson',

    getRecordData: function(record, operation) {
        
        if (record.writeStructuredData) {
            return record.getWriteData(operation);
        } else {
            return this.callParent(arguments);
        }
    }
});