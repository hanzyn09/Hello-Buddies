var alertMessage = "";
var action = "";

// 각 열의 정렬 상태를 관리하는 객체
var sortDirections = {
	0: 'asc',
	1: 'asc',
	2: 'asc',
	3: 'asc',
	4: 'asc'
};

// 타이틀 클릭 시 현재 페이지 새로 고침
function refreshPage() {
	location.reload(); // 페이지 새로 고침
}

// 테이블 정렬
function sortTable(columnIndex) {
	var table = document.getElementById("buddyTable");
	var rows = table.getElementsByTagName("tr");
	var switching = true;
	var dir = sortDirections[columnIndex]; // 해당 열의 현재 정렬 방향

	while (switching) {
		switching = false;
		var rowsArray = Array.from(rows).slice(1); // 헤더를 제외한 행들을 배열로 변환

		for (var i = 0; i < rowsArray.length - 1; i++) {
			var x = rowsArray[i].getElementsByTagName("td")[columnIndex];
			var y = rowsArray[i + 1].getElementsByTagName("td")[columnIndex];

			var xContent = x ? x.textContent.trim() : '';
			var yContent = y ? y.textContent.trim() : '';

			var shouldSwitch = false;
			if (isNaN(xContent) || isNaN(yContent)) {
				// 문자열 비교
				if ((dir === "asc" && xContent.toLowerCase() > yContent.toLowerCase()) ||
					(dir === "desc" && xContent.toLowerCase() < yContent.toLowerCase())) {
					shouldSwitch = true;
				}
			} else {
				// 숫자 비교
				if ((dir === "asc" && parseInt(xContent) > parseInt(yContent)) ||
					(dir === "desc" && parseInt(xContent) < parseInt(yContent))) {
					shouldSwitch = true;
				}
			}

			if (shouldSwitch) {
				rowsArray[i].parentNode.insertBefore(rowsArray[i + 1], rowsArray[i]);
				switching = true;
				break;
			}
		}

		if (!switching) {
			sortDirections[columnIndex] = (dir === "asc" ? "desc" : "asc");
		}
	}
}

// 이름 검색 기능
function searchBuddy() {
	var input = document.getElementById('searchInput');
	var filter = input.value.toLowerCase();
	var table = document.getElementById('buddyTable');
	var rows = table.getElementsByTagName('tr');
	var noDataRow = document.getElementById('noDataRow'); // 안내 문구

	var found = false;

	for (var i = 1; i < rows.length; i++) {
		var td = rows[i].getElementsByTagName('td')[1]; // 이름 열
		if (td) {
			var txtValue = td.textContent || td.innerText;
			// 검색어에 맞는 이름을 찾으면 해당 행을 표시하고, 아니면 숨깁니다.
			if (txtValue.toLowerCase().includes(filter)) {
				rows[i].style.display = "";  // 검색어가 포함된 경우 표시
				found = true;
			} else {
				rows[i].style.display = "none";  // 검색어가 포함되지 않은 경우 숨김
			}
		}
	}

	// 검색 결과가 없으면 안내 문구 표시
	noDataRow.style.display = found ? "none" : "table-row"; // 검색 결과가 있으면 안내 문구 숨기고, 없으면 표시

	// 버디 수 업데이트
	updateActiveBuddyCount();
}

// 공통 폼 제출 함수
function submitForm(action, state) {
	let frm = $("#frm")[0];
	frm.action = action;

	let stateInput = frm.querySelector('input[name="state"]');
	if (stateInput) {
		stateInput.value = state;
	} else {
		alertMessage = "폼에 'state' 필드가 없습니다.";
		action = "error";
		displayAlert(alertMessage, action);
	}

	frm.submit(); // 폼 제출
}

// 하루 건너뛰기 버튼 클릭 시
$(function() {
	$("#btnDay").on("click", function(event) {
		event.preventDefault();

		var buddyRows = document.getElementById("buddyTableBody").getElementsByTagName("tr");
		var hasData = Array.from(buddyRows).some(row => row.style.display !== 'table-row' && row.id !== 'noDataRow');

		if (!hasData) {
			alertMessage = "현재 키우는 버디가 없습니다.<br>새로 입양해주세요.";
			action = "error";
			displayAlert(alertMessage, action);
		} else {
			alertMessage = '하루를 건너뜁니다.<br><button id="confirmAdopt" class="btn btn-success">확인</button><button id="cancelAdopt" class="btn btn-danger">취소</button>';
			action = "info";
			displayAlert(alertMessage, action);

			$("#confirmAdopt").on("click", function() {
				submitForm("updateDate.do", "day");
				toastr.clear();
			});

			$("#cancelAdopt").on("click", function() {
				toastr.clear();
			});
		}
	});
});

// 버디 수 업데이트 함수
function updateActiveBuddyCount() {
	var activeBuddyCount = Array.from(document.querySelectorAll('#buddyTable tbody tr'))
		.filter(row => row.id !== 'noDataRow' && row.getAttribute('data-deleted') !== 'true')
		.length;

	document.getElementById('activeBuddyCount').textContent = activeBuddyCount;
}

// 데이터를 30초마다 갱신하는 함수
function fetchBuddy() {
	$.ajax({
		url: '/buddy/fetchBuddy.do',
		method: 'GET',
		success: function(data) {
			alertMessage = "하루가 경과했습니다!<br>버디들의 상태를 확인해주세요.";
			action = "info";
			displayAlert(alertMessage, action);

			updateTable(data);
		},
		error: function() {
			console.log("데이터를 가져오는 데 실패했습니다.");
		}
	});
}

// 테이블을 최신 데이터로 업데이트하는 함수
function updateTable(data) {
    const tbody = document.getElementById('buddyTableBody');
    const noDataRow = document.getElementById('noDataRow');  // '조회된 버디가 없습니다.' 안내 문구

    // 'noDataRow'가 null인지 확인
    if (noDataRow) {
        // 기존 데이터 지우기 (noDataRow를 제외한 나머지 행들 삭제)
        Array.from(tbody.getElementsByTagName('tr')).forEach(row => {
            if (row !== noDataRow) {
                tbody.removeChild(row);
            }
        });

        // 데이터가 없거나 빈 배열인 경우 안내 문구 표시
        if (!data || (Array.isArray(data) && data.length === 0)) {
            noDataRow.style.display = "table-row";  // 데이터가 없으면 안내 문구 표시
        } else {
            noDataRow.style.display = "none"; // 데이터가 있으면 안내 문구 숨기기
            // 새로 받은 데이터를 테이블에 추가
            data.forEach(buddy => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${buddy.buddyId}</td>
                    <td>${buddy.name}</td>
                    <td>Lv. ${buddy.levelNumber}</td>
                    <td style="color: ${buddy.hunger >= 80 ? 'red' : 'black'}" title="Hunger(은)는 0%가 최고의 상태입니다." id="hunger">${buddy.hunger}%</td>
                    <td style="color: ${buddy.fatigue >= 80 ? 'red' : 'black'}" title="Fatigue(은)는 0%가 최고의 상태입니다." id="fatigue">${buddy.fatigue}%</td>
                    <td style="color: ${buddy.happiness <= 30 ? 'red' : 'black'}" title="Happiness(은)는 100%가 최고의 상태입니다." id="happiness">${buddy.happiness}%</td>
                    <td>
                        <span class="status ${((100 - buddy.hunger) * 0.2 + (100 - buddy.fatigue) * 0.3 + buddy.happiness * 0.5) / 3 >= 16.6 ? 'active' : 'inactive'}">
                            <span class="${((100 - buddy.hunger) * 0.2 + (100 - buddy.fatigue) * 0.3 + buddy.happiness * 0.5) / 3 >= 16.6 ? 'fas fa-smile-beam fa-2x' : 'fas fa-sad-tear fa-2x'}"></span>
                        </span>
                    </td>
                    <td>
                        <a href="/buddy/openBuddyDetail.do?buddyId=${buddy.buddyId}" class="btn btn-secondary">보살피기</a>
                    </td>
                `;
                tbody.appendChild(row);
            });
        }

        // 버디 수 업데이트
        updateActiveBuddyCount();
    } else {
        console.error("noDataRow element not found!");
    }
}

$(document).ready(function() {
	// 페이지 로드 시 테이블의 데이터 상태를 확인
	var buddyRows = document.getElementById("buddyTableBody").getElementsByTagName("tr");
	var noDataRow = document.getElementById('noDataRow');

	if (noDataRow) {
		// 데이터가 있으면 '조회된 버디가 없습니다.'를 숨기고, 없으면 보이게
		var hasData = Array.from(buddyRows).some(row => row.id !== 'noDataRow' && row.style.display !== 'none');

		// 'noDataRow' 상태 변경
		noDataRow.style.display = hasData ? "none" : "table-row";
	} else {
		console.error("noDataRow element not found during page load!");
	}

	// 버디 수 업데이트
	updateActiveBuddyCount();

	// 데이터 30초마다 갱신
	setInterval(fetchBuddy, 30000); // 30초마다 데이터 갱신
});
