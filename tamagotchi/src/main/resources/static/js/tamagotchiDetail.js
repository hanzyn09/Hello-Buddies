var alertMessage = "";
var action = "";

$(function() {
	// 공통 폼 제출 함수
	function submitForm(action, state) {
		let frm = $("#frm")[0];  // 폼 선택
		frm.action = action;      // 폼의 action 속성 설정

		// 'state' 필드에 전달받은 값 설정
		let stateInput = frm.querySelector('input[name="state"]');
		if (stateInput) {
			stateInput.value = state;  // state 필드에 값 설정
		} else {
			alertMessage= "폼에 'state' 필드가 없습니다."
			action = "error";
			
			displayAlert(alertMessage, action); //alert 
		}

		frm.submit();  // 폼 제출
	}

	// 각 버튼 클릭 시 해당 상태 값 전달 및 폼 제출
	$("#btnHunger").on("click", function(event) {
		event.preventDefault();  // 기본 동작 방지
		submitForm("updateState.do", "hunger");  // 'hunger' 값 전달
	});

	$("#btnSleep").on("click", function(event) {
		event.preventDefault();  // 기본 동작 방지
		submitForm("updateState.do", "sleep");  // 'sleep' 값 전달
	});

	$("#btnPlay").on("click", function(event) {
		event.preventDefault();  // 기본 동작 방지
		submitForm("updateState.do", "play");  // 'play' 값 전달
	});

	$("#btnDelete").on("click", function(event) {
	    event.preventDefault();  // 기본 동작 방지
		
		alertMessage= '다마고치를 입양 보냅니다.<br><button id="confirmAdopt" class="btn btn-success">확인</button><button id="cancelAdopt" class="btn btn-danger">취소</button>';
		action = "info";				
		displayAlert(alertMessage, action); //alert

	    // 확인 버튼 클릭 시 폼 제출
	    $("#confirmAdopt").on("click", function() {
	        submitForm("updateState.do", "delete");  // 'delete' 값 전달
	        toastr.clear(); // 알림창 닫기
	    });

	    // 취소 버튼 클릭 시 알림 창 닫기
	    $("#cancelAdopt").on("click", function() {
	        toastr.clear(); // 알림창 닫기
	    });
	});


	// 목록 페이지로 이동
	$("#btnList").on("click", function(event) {
		event.preventDefault();  // 기본 동작 방지
		location.href = "openTamagotchiList.do";  // 목록 페이지로 이동
	});
});

$(document).ready(function() {
    const tamagotchiId = $('input[name="tamagotchiId"]').val();  // 타마고치 ID 가져오기
    
    // 서버에서 타마고치 상태값을 가져오는 함수
    function getTamagotchiStatus() {
        $.ajax({
            url: '/tamagotchi/getTamagotchiStatus.do',  // 서버 엔드포인트
            method: 'GET',
            data: { tamagotchiId: tamagotchiId },  // 타마고치 ID 전달
            success: function(data) {
                // 서버에서 받은 타마고치 상태 갱신
				$('#levelNumber').text('Lv. '+ data.levelNumber);
                $('#hunger').text(data.hunger + '%').css('color', data.hunger >= 80 ? 'red' : 'black');
                $('#fatigue').text(data.fatigue + '%').css('color', data.fatigue >= 80 ? 'red' : 'black');
                $('#happiness').text(data.happiness + '%').css('color', data.happiness <= 30 ? 'red' : 'black');

                // 타마고치 상태에 따라 클래스 변경 (행복한지 슬픈지 표시)
                const averageStatus = (100 - data.hunger) * 0.2 + (100 - data.fatigue) * 0.3 + data.happiness * 0.5;
                const statusClass = (averageStatus / 3 >= 16.6) ? 'happy' : 'sad';
                $('.tamagotchi-status').attr('class', 'tamagotchi-status ' + statusClass);

                // 상태 메시지 업데이트
				const statusMessage = (averageStatus / 3 >= 16.6) ? "\"너무 행복해요!\"" : "\"슬퍼요, 도와주세요!\"";
                $('.tamagotchi-status span').text(statusMessage);
            },
            error: function(error) {
                console.log("Error fetching tamagotchi status:", error);
            }
        });
    }

    setInterval(function() {
		alertMessage = "하루가 경과했습니다!<br>타마고치 상태를 확인해주세요.";
		action = "info";
		displayAlert(alertMessage, action); //alert
		
        getTamagotchiStatus();
    }, 30000);  // 30초마다 실행 (30000ms)

    // 페이지가 로드되었을 때 상태를 한 번 불러오기
    getTamagotchiStatus();
});

