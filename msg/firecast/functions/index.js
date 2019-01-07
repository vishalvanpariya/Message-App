'use-strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('{user_1}/{user_2}/{msg_id}').onWrite((change,context)=>{
    const user = context.params.user_1;
    const user2 = context.params.user_2;
    const msg_id = context.params.msg_id;
    

    if(user.toString() != 'fortocken'){
        return admin.database().ref(`${user}/${user2}/${msg_id}`).once('value',snapshot =>{
            const obj = snapshot.val();
            if(obj.flag.toString() === 'true'){
                admin.database().ref(`fortocken/${user2}/token`).once('value',snap =>{
                    const token_id = snap.val();
                    const payload = {
                        notification:{
                            title:"From :"+ user.toString(),
                            body:obj.msg,
                            icon:'default',
                            click_action:"android.intent.action.MAIN"
                        },
                        data :{
                            from_id:user
                        }
                    };
                    return admin.messaging().sendToDevice(token_id,payload).then(result =>{
                        console.log('Notification Send.');
                    });
                 });
            }
        });
    }
});