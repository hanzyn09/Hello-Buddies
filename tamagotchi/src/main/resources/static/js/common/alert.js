const timeOut = 3000; // 3초 후 사라짐
const extendedTimeOut = 3000; // 마우스 hover 시 사라지는 시간

// alertMessage가 있으면 toastr을 실행하는 함수
function displayAlert(alertMessage, action) {
    if (alertMessage) {
        switch (action) {
            case "success":
                toastr.success(alertMessage, {
                    closeButton: true,
                    progressBar: true,
                    timeOut: timeOut,
                    extendedTimeOut: extendedTimeOut,
                    enableHtml: true
                });
                break;
            case "info":
                toastr.info(alertMessage, "확인", {
                    closeButton: true,
                    progressBar: true,
                    timeOut: 10000,
                    extendedTimeOut: 10000,
                    enableHtml: true
                });
                break;
            case "error":
                toastr.error(alertMessage, {
                    closeButton: true,
                    progressBar: true,
                    timeOut: timeOut,
                    extendedTimeOut: extendedTimeOut,
                    enableHtml: true
                });
                break;
            default:
                console.log("Invalid toastr action.");
        }
    }
}
