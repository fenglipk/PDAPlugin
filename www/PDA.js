var pda = {
    read: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'PDA', // mapped to our native Java class called "CalendarPlugin"
            'read', // with this action name
            []
        ); 
    },
	cancelRead: function(successCallback, errorCallback) {                         
		cordova.exec(                                                              
			successCallback, // success callback function                          
			errorCallback, // error callback function                              
			'PDA', // mapped to our native Java class called "CalendarPlugin" 
			'cancelRead', // with this action name                                 
			[]                                                                     
		);                                                                         
	}                                                                              
}
module.exports = pda;