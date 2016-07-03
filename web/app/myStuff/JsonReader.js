/**
 * A variant of the JSON reader. Instead of reading arrays, where each record in the array field
 * has an 'id' property, it reads objects - aka associative arrays. The key of the entry will be the
 * array.
 *
 * So where the JSON reader would like data like this:
 * [ { id: '1', property: 'foo' }, { id: '2', property: 'bar' } ]
 *
 * the associative reader likes data like this:
 * { '1': { property: 'foo' }, '2': { property: 'bar' } }
 */
Ext.define('AM.myStuff.JsonReader', {
    extend: 'Ext.data.reader.Json',
    alias: 'reader.associative',
    
    readRecords: function(data) {
// convert the associative array into a normal array.
        var idProperty = 'id'; // should be a config value?
        var arrayData = []
        Ext.Object.each(data, function(key, value) {
            var arrayEntry = {};
            Ext.Object.merge(arrayEntry, value);
            arrayEntry[idProperty] = key;
            arrayData.push(arrayEntry);
        });
        return this.callParent([arrayData]);
    }
}) 