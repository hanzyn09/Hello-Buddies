
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
			alert("폼에 'state' 필드가 없습니다.");  // state 필드가 없으면 경고
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
		alert("다마고치를 입양 보냅니다.");  // 사용자에게 메시지 표시
		submitForm("updateState.do", "delete");  // 'delete' 값 전달
	});

	// 목록 페이지로 이동
	$("#btnList").on("click", function(event) {
		event.preventDefault();  // 기본 동작 방지
		location.href = "openTamagotchiList.do";  // 목록 페이지로 이동
	});
});

/*
		// 10초마다 전체 타마고치 목록을 갱신하는 AJAX 요청
		setInterval(function() {
			$.ajax({
				url: '/tamagotchi/openTamagotchiDetail.do',  // 전체 타마고치 리스트를 반환하는 서버 엔드포인트
				method: 'GET',
				success: function(tamagotchi) {
					// 서버에서 받은 JSON 데이터로 테이블 갱신
					$('#hunger').text(tamagotchi.hunger + '%');
					$('#fatigue').text(tamagotchi.fatigue + '%');
					$('#happiness').text(tamagotchi.happiness + '%');

					// 각 값의 색상 업데이트
					$('#hunger').css('color', tamagotchi.hunger >= 80 ? 'red' : 'black');
					$('#fatigue').css('color', tamagotchi.fatigue >= 80 ? 'red' : 'black');
					$('#happiness').css('color', tamagotchi.happiness <= 30 ? 'red' : 'black');
				},
				error: function(error) {
					console.log("Error:", error);
				}
			});
		}, 10000);  // 10초마다 요청
		*/