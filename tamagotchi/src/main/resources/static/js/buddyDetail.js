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
			alertMessage = "폼에 'state' 필드가 없습니다."
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

		alertMessage = '입양 보내시겠습니까?<br><button id="confirmAdopt" class="btn btn-success">확인</button><button id="cancelAdopt" class="btn btn-danger">취소</button>';
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
		location.href = "openBuddyList.do";  // 목록 페이지로 이동
	});
});

// 서버에서 버디 상태값을 가져오는 함수
function fetchBuddyDetail() {
	const buddyId = $('input[name="buddyId"]').val();  // 버디 ID 가져오기

	$.ajax({
		url: '/buddy/fetchBuddyDetail.do',  // 서버 엔드포인트
		method: 'GET',
		data: { buddyId: buddyId },  // 버디 ID 전달
		success: function(data) {
			alertMessage = "하루가 경과했습니다!<br>버디의 상태를 확인해주세요."
			action = "info";
			displayAlert(alertMessage, action); //alert 

			updateTable(data);
		},
		error: function(error) {
			console.log("Error fetching buddy status:", error);
		}
	});
}
// 테이블을 최신 데이터로 업데이트하는 함수
function updateTable(data) {
	// 서버에서 받은 버디 상태 갱신
	$('#levelNumber').text('Lv. ' + data.levelNumber);
	$('#hunger').text(data.hunger + '%').css('color', data.hunger >= 80 ? 'red' : 'black');
	$('#fatigue').text(data.fatigue + '%').css('color', data.fatigue >= 80 ? 'red' : 'black');
	$('#happiness').text(data.happiness + '%').css('color', data.happiness <= 30 ? 'red' : 'black');

	// 버디 상태에 따라 클래스 변경 (행복한지 슬픈지 표시)
	const averageStatus = (100 - data.hunger) * 0.2 + (100 - data.fatigue) * 0.3 + data.happiness * 0.5;
	const statusClass = (averageStatus / 3 >= 16.6) ? 'happy' : 'sad';
	$('.buddy-status').attr('class', 'buddy-status ' + statusClass);

	// 상태 메시지 업데이트
	const statusMessage = (averageStatus / 3 >= 16.6) ? "\"너무 행복해요!\"" : "\"슬퍼요, 도와주세요!\"";
	$('.buddy-status span').text(statusMessage);
}
// 페이지 로드 시 최초 데이터를 가져오고, 30초마다 갱신
$(document).ready(function() {
	setInterval(fetchBuddyDetail, 30000); // 30초마다 데이터 갱신
});
