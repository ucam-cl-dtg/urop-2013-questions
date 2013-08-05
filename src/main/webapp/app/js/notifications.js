var notificationOptions = {
    autoHide : true,
    clickOverlay : false,
    MinWidth : 250,
    TimeShown : 3000,
    ShowTimeEffect : 200,
    HideTimeEffect : 200,
    LongTrip :20,
    HorizontalPosition : 'right',
    VerticalPosition : 'top',
    ShowOverlay : false,
    ColorOverlay : '#000',
    OpacityOverlay : 0.3,
    onClosed : function(){

    },
    onCompleted : function(){

    }
}
function successNotification(message) {
    jSuccess(
        message,
        notificationOptions
    );
}

function errorNotification(message) {
    jError(
        message,
        notificationOptions
    )
}

function showNotification(message) {
    jNotify(
        message,
        notificationOptions
    )
}
