Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath("AM", "app");
console.log(Ext.getVersion());
Ext.require( ['AM.model.CrudOperation','AM.myStuff.JsonWriter','AM.myStuff.TransactionProxy','AM.myStuff.WebSocketManager','AM.myStuff.WebSocketManaged','AM.view.user.ApplicationPanel','Ext.data.association.HasMany','Ext.data.association.BelongsTo']);
//AM.myStuff.JsonWriter','AM.myStuff.BaseModel'
Ext.application({

    name: 'AM',
    appFolder: 'app',
    controllers: ['Users'],

    launch: function() {


        console.log('lauching application '+Ext.FocusManager.enabled);
        Ext.create('Ext.container.Viewport', {
            layout: 'anchor',
            items: [
                {
                    xtype: 'masterView',
                    anchor: '100% 100%'
                }
            ]
        });
    }
});
