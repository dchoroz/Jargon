Ext.define('AM.myStuff.BaseModel', {
    extend: 'Ext.data.Model',
    writeStructuredData: true,
    
    /*Returns record's full data joined with the dirty-phantom associated records*/
    getWriteData: function(operation) {
        var data = this.getRecordWriteData(this, true),
                associations = this.associations.items,
                action = operation.action,
                association, type, name, associatedStore,
                associatedRecords, associatedRecord, recordData,
                a, aLen, r, rLen, count;
        if(action === 'destroy'){
            return data;
        } else {
            for (a = 0, aLen = associations.length; a < aLen; a++) {
                association = associations[a];
                type = association.type;
                name = association.name;

                if (type == 'hasMany') {
                    associatedStore = this[association.storeName];
                // Initialize the array for this association
                    

                // If the association's loaded, process its records
                    if (associatedStore && associatedStore.getCount() > 0) {
                        associatedRecords = associatedStore.data.items;
                        data[name] = [];
                    // Append data for each record
                        count = 0;
                        for (r = 0, rLen = associatedRecords.length; r < rLen; r++) {
                            if( action === 'create' || (action === 'update' && (associatedRecords[r].dirty || associatedRecords[r].phantom))){
                                recordData = this.getRecordWriteData(associatedRecords[r], false);
                                if (recordData != null) {
                                    data[name][count] = recordData
                                    count++;
                                }
                            }
                        }
                        alert(data[name].length == 0);
                        if(data[name].length == 0){
                            data[name] = undefined;
                        }
                    }
                } else if (type == 'hasOne') {
                    associatedRecord = this[association.instanceName];
                // If the record exists, append its data
                    if (associatedRecord !== undefined) {
                        if( action === 'create' || (action === 'update' && (associatedRecord.dirty || associatedRecord.phantom))){
                            data[name] = this.getRecordWriteData(associatedRecord, false);
                        }
                    }
                }

            }
            return data;
        }
    },
            
    /*Returns the fields if a specific record if the record is phantom-dirty or forced*/
    getRecordWriteData: function(record, forceWrite) {
        var isPhantom = record.phantom === true,
                writeAllFields = record.writeAllFields,
                fields = record.fields,
                fieldItems = fields.items,
                data = {},
                changes = record.getChanges(),
                field,
                key,
                f, fLen;

        if (record.phantom || record.dirty || forceWrite) {
            for (f = 0, fLen = fieldItems.length; f < fLen; f++) {
                field = fieldItems[f];

                if (field.forcePersist || (field.persist && (writeAllFields || isPhantom))) {
                    this.setFieldWriteData(data, record, field, record.get(field.name));
                }
            }

            for (key in changes) {
                if (changes.hasOwnProperty(key)) {
                    field = fields.get(key);
                    if (field.persist) {
                        this.setFieldWriteData(data, record, field, changes[key]);
                    }
                }
            }

            return data;
        } else {
            return null;
        }

    },
    
    /*Select data of the field*/        
    setFieldWriteData: function(data, record, field, value) {
        var name = field[this.nameProperty] || field.name,
                path, i, len, curr;

        if (field.name === record.idProperty && record.phantom) {
            return;
        }

        if (field.mapping) {
            if (field.mapping.indexOf('.')) {
                path = field.mapping.split('.');
                curr = data;
                for (i = 0, len = path.length - 1; i < len; i++) {
                    if (!curr[path[i]]) {
                        curr[path[i]] = {};
                    }
                    curr = curr[path[i]];
                }
                curr[path[i]] = value;
            }
        } else {
            data[name] = value;
        }
    },
    
    /*Overrides Model's copyFrom method, so I can cpy associated data too*/        
    copyFrom: function(sourceRecord) {
        var me = this,
                fields = me.fields.items,
                fieldCount = fields.length,
                modifiedFieldNames = [],
                field, i = 0,
                myData,
                sourceData,
                idProperty = me.idProperty,
                name,
                value,
                clientAssociations = this.associations.items,
                sourceAssociations, clientRecordData,
                association, type, name, clientAssociatedStore, sourceAssociatedStore,
                clientAssociatedRecords, sourceAssociatedRecords, clientAssociatedRecord, sourceAssociatedRecord,
                recordData, a, aLen, r, rLen, index;


//        console.log('Model.copyFrom');
//        console.log(sourceRecord);
//        console.log(this);

        if (sourceRecord) {
            myData = me[me.persistenceProperty];
            sourceData = sourceRecord[sourceRecord.persistenceProperty];
            for (; i < fieldCount; i++) {
                field = fields[i];
                name = field.name;

                // Do not use setters.
                // Copy returned values in directly from the data object.
                // Converters have already been called because new Records
                // have been created to copy from.
                // This is a direct record-to-record value copy operation.
                // don't copy the id, we'll do it at the end
                if (name != idProperty) {
                    value = sourceData[name];

                    // If source property is specified, and value is different
                    // copy field value in and build updatedFields
                    if (value !== undefined && !me.isEqual(myData[name], value)) {
                        myData[name] = value;
                        modifiedFieldNames.push(name);
                    }
                }

            }
                
                for (a = 0, aLen = clientAssociations.length; a < aLen; a++) {

                    association = clientAssociations[a];
                    type = association.type;
                    name = association.name;

                    if (type == 'hasMany') {
                        
                        sourceAssociatedStore = sourceRecord[association.storeName];
  
                        if (sourceAssociatedStore && sourceAssociatedStore.getCount() > 0) {
                            clientAssociatedStore = this[association.storeName];
                            // Initialize the array for this association
                            //data[name] = [];
                            // If the association's loaded, process its records,## Not needed because records may come from another client
                            //if (clientAssociatedStore && clientAssociatedStore.getCount() > 0) {
                                clientAssociatedRecords = clientAssociatedStore.data.items;
                                sourceAssociatedRecords = sourceAssociatedStore.data.items;
                                // Append data for each record
                                index = 0;
                                for (r = 0, rLen = clientAssociatedRecords.length; r < rLen; r++) {
                                    clientRecordData = clientAssociatedRecords[r];
                                    alert(clientRecordData.phantom);
                                    if(clientRecordData.phantom === true || (clientRecordData.dirty === true && (clientRecordData[this.idProperty] === sourceAssociatedRecords[index][this.idProperty]))){
                                        clientRecordData.copyFrom(sourceAssociatedRecords[index]);
                                        index++;
                                    }
                                    if(index > sourceAssociatedStore.getCount()){
                                        break;
                                    }
                                }
                                if(index < sourceAssociatedStore.getCount()){
                                    clientAssociatedStore.loadData(sourceAssociatedRecords.slice(index, sourceAssociatedStore.getCount()),true);
                                }
                            //}
                        }
                    } else if (type == 'hasOne') {
                        clientAssociatedRecord = this[association.instanceName];
                        sourceAssociatedRecord = sourceRecord[association.instanceName];
                        // If the record exists, append its data
                        if (sourceAssociatedRecord !== undefined) {
                            if (clientAssociatedRecord !== undefined) {
                                clientAssociatedRecord.copyFrom(sourceAssociatedRecord);
                            }else{
                                this[association.instanceName] = sourceAssociatedRecord;
                            }
                        }
                    }

                }

                // If this is a phantom record being updated from a concrete record, copy the ID in.
                if (me.phantom && !sourceRecord.phantom) {
                    // beginEdit to prevent events firing
                    // commit at the end to prevent dirty being set
                    me.beginEdit();
                    me.setId(sourceRecord.getId());
                    me.endEdit(true);
                    me.commit(true);
                }
            }
        //}
        return modifiedFieldNames;
    }

});
