// 타이틀 클릭 시 현재 페이지 새로 고침
function refreshPage() {
	location.reload(); // 페이지 새로 고침
}

// 각 열의 정렬 상태를 관리하는 객체
var sortDirections = {
	0: 'asc',
	1: 'asc',
	2: 'asc',
	3: 'asc',
	4: 'asc'
};

function sortTable(columnIndex) {
	var table = document.getElementById("tamagotchiTable");
	var rows = table.getElementsByTagName("tr");
	var switching = true;
	var shouldSwitch;
	var dir = sortDirections[columnIndex]; // 해당 열의 현재 정렬 방향

	// 계속해서 테이블을 반복하며 정렬을 시도합니다.
	while (switching) {
		switching = false;
		var rowsArray = Array.from(rows).slice(1); // 헤더를 제외한 행들을 배열로 변환

		// 모든 행을 하나씩 비교하여 정렬
		for (var i = 0; i < rowsArray.length - 1; i++) {
			var x = rowsArray[i].getElementsByTagName("td")[columnIndex];
			var y = rowsArray[i + 1].getElementsByTagName("td")[columnIndex];

			shouldSwitch = false;

			var xContent = x ? x.textContent.trim() : '';
			var yContent = y ? y.textContent.trim() : '';

			if (!isNaN(xContent) && !isNaN(yContent)) {
				if (dir === "asc") {
					if (parseInt(xContent) > parseInt(yContent)) {
						shouldSwitch = true;
						break;
					}
				} else if (dir === "desc") {
					if (parseInt(xContent) < parseInt(yContent)) {
						shouldSwitch = true;
						break;
					}
				}
			} else {
				if (dir === "asc") {
					if (xContent.toLowerCase() > yContent.toLowerCase()) {
						shouldSwitch = true;
						break;
					}
				} else if (dir === "desc") {
					if (xContent.toLowerCase() < yContent.toLowerCase()) {
						shouldSwitch = true;
						break;
					}
				}
			}
		}

		if (shouldSwitch) {
			rowsArray[i].parentNode.insertBefore(rowsArray[i + 1], rowsArray[i]);
			switching = true;
		} else {
			// 방향이 변경될 때마다 정렬 방향을 반대로 토글
			if (dir === "asc") {
				sortDirections[columnIndex] = "desc";
			} else {
				sortDirections[columnIndex] = "asc";
			}
			switching = false;
		}
	}
}

// 이름 검색 기능
function searchTamagotchi() {
	var input, filter, table, tr, td, i, txtValue;
	input = document.getElementById('searchInput');
	filter = input.value.toLowerCase();
	table = document.getElementById('tamagotchiTable');
	tr = table.getElementsByTagName('tr');
	var noDataRow = document.getElementById('noDataRow'); // 안내 문구

	var found = false; // 검색된 항목이 있는지 확인하는 변수

	for (i = 1; i < tr.length; i++) { // 첫 번째 tr은 헤더이므로 건너뜁니다.
		td = tr[i].getElementsByTagName('td')[1]; // 이름 열
		if (td) {
			txtValue = td.textContent || td.innerText;
			if (txtValue.toLowerCase().indexOf(filter) > -1) {
				tr[i].style.display = "";
				found = true; // 검색 결과가 있으면 true로 설정
			} else {
				tr[i].style.display = "none";
			}
		}
	}

	// 검색 결과가 없으면 안내 문구를 보이게 함
	if (!found) {
		noDataRow.style.display = "";
	} else {
		noDataRow.style.display = "none";
	}

	// 삭제되지 않은 타마고치 수를 다시 계산하고 업데이트
	updateActiveTamaCount();
}

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
	
	// 하루 건너뛰기 버튼 클릭 시
	$("#btnDay").on("click", function(event) {
		event.preventDefault(); // 기본 동작 방지
	
		// 타마고치가 있는지 확인
		var tamagotchiRows = document.getElementById("tamagotchiTableBody").getElementsByTagName("tr");
		var hasData = false;
		for (var i = 0; i < tamagotchiRows.length; i++) {
			// 'noDataRow' 행을 제외하고 데이터가 있는지 확인
			if (tamagotchiRows[i].style.display !== 'none' && tamagotchiRows[i].id !== 'noDataRow') {
				hasData = true;
				break;
			}
		}
	
		// 데이터가 없으면 경고 메시지 표시하고 동작 안 함
		if (!hasData) {
			alert("현재 키우는 다마고치가 없습니다. 다마고치를 추가해주세요.");
		} else {
			alert("하루 건너뛰기 버튼을 클릭했습니다. 다마고치 상태가 변화합니다.");
			let frm = $("#frm")[0];
			submitForm("updateDate.do", "day");  // 'day' 값 전달
		}
	});
});

// 페이지 로드 시 데이터가 없는지 확인하고, 없으면 "조회된 다마고치가 없습니다." 안내 문구 표시
document.addEventListener("DOMContentLoaded", function() {
	// 페이지 로딩 시 타마고치 데이터가 없으면 안내 문구 표시
	var tamagotchiRows = document.getElementById("tamagotchiTableBody").getElementsByTagName("tr");
	var noDataRow = document.getElementById('noDataRow'); // '조회된 다마고치가 없습니다.' 안내 문구

	// 데이터가 없다면 안내 문구를 보이게
	var hasData = false;
	for (var i = 0; i < tamagotchiRows.length; i++) {
	    // 데이터 행이 하나라도 있으면 hasData를 true로 설정
	    if (tamagotchiRows[i].style.display !== 'none' && tamagotchiRows[i].id !== 'noDataRow') {
	        hasData = true;
	        break;
	    }
	}

	if (hasData) {
	    noDataRow.style.display = "none";  // 데이터가 있으면 안내 문구 숨기기
	} else {
	    noDataRow.style.display = "";  // 데이터가 없으면 안내 문구 표시
	}
	
	updateActiveTamaCount(); // 페이지 로드 시 타마고치 수 업데이트
});

// 타마고치 수 업데이트 함수
function updateActiveTamaCount() {
	var activeTamaCount = 0;
	var rows = document.querySelectorAll('#tamagotchiTable tbody tr');

	// '조회된 다마고치가 없습니다.'를 제외한 나머지 tr만 카운트
	rows.forEach(function(row) {
		// 'noDataRow' id를 가진 행은 제외
		if (row.id !== 'noDataRow') {
			var isDeleted = row.getAttribute('data-deleted') === 'true'; // 데이터 속성으로 삭제 여부 확인
			if (!isDeleted) { // 삭제되지 않은 것만 카운트
				activeTamaCount++;
			}
		}
	});

	document.getElementById('activeTamaCount').textContent = activeTamaCount; // 갯수 업데이트
}

// 데이터가 없는지 확인하여 "조회된 다마고치가 없습니다." 안내 문구 표시
document.addEventListener("DOMContentLoaded", function() {
	var tamagotchiRows = document.getElementById("tamagotchiTableBody").getElementsByTagName("tr");
	var noDataRow = document.getElementById('noDataRow');

	// 데이터가 없다면 안내 문구를 보이게
	var hasData = false;
	for (var i = 0; i < tamagotchiRows.length; i++) {
		// 데이터 행이 하나라도 있으면 hasData를 true로 설정
		if (tamagotchiRows[i].id !== 'noDataRow' && tamagotchiRows[i].style.display !== 'none') {
			hasData = true;
			break;
		}
	}

	if (hasData) {
		noDataRow.style.display = "none";
	} else {
		noDataRow.style.display = "";
	}
});

$(document).ready(function() {
    // 30초마다 "하루가 경과했습니다!" 알림을 띄우고 화면을 새로 고침
    setInterval(function() {
        toastr.info("하루가 경과했습니다! 타마고치 상태를 확인해주세요.");
        location.reload();  // 페이지 새로 고침
    }, 30000);  // 30000ms = 60초마다 호출
});