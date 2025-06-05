// src/main/resources/static/assets/js/admindetailset.js

const baseUrl = ''; // nếu front + back cùng host thì để trống

document.addEventListener('DOMContentLoaded', () => {
  const token = sessionStorage.getItem('token');
  if (!token) {
    // Nếu chưa login hoặc token hết hạn, redirect về login
    window.location.href = 'sign-in.html';
    return;
  }

  // 1) Lấy setId từ query string: ?setId=123
  const params = new URLSearchParams(window.location.search);
  const setId = params.get('setId');
  if (!setId) {
    alert('Không tìm thấy ID của bộ flashcard.');
    history.back();
    return;
  }

  // 2) Tham chiếu các phần tử DOM
  const setTitleEl = document.getElementById('setTitle');
  const setTitleNavEl = document.getElementById('setTitleNav');
  const toggleBtn = document.getElementById('toggleVisibilityBtn');
  const cardsTbody = document.getElementById('cardsTbody');

  // Biến lưu trạng thái hiện tại (visible) để toggle
  let currentVisible = true;

  // helper tạo headers cho fetch
  const authHeaders = () => ({
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  });

  // 3) Hàm fetch chi tiết bộ flashcard
  function fetchSetDetail() {
    fetch(`${baseUrl}/api/admin/flashcardsets/${setId}`, {
      method: 'GET',
      headers: authHeaders()
    })
      .then(res => {
        if (res.status === 401 || res.status === 403) {
          // token hết hạn hoặc không phải ADMIN
          window.location.href = 'sign-in.html';
          throw new Error('Unauthorized');
        }
        if (!res.ok) {
          throw new Error(`HTTP ${res.status}`);
        }
        return res.json();
      })
      .then(data => {
        // data: FlashcardSetDetailDTO từ backend
        renderSetDetail(data);
      })
      .catch(err => {
        console.error('Lỗi khi lấy chi tiết bộ:', err);
        // Hiển thị lỗi đơn giản
        cardsTbody.innerHTML = `
          <tr>
            <td colspan="2" class="text-center text-danger">Không tải được chi tiết bộ.</td>
          </tr>`;
      });
  }

  // 4) Hàm render chi tiết lên giao diện
  function renderSetDetail(dto) {
    // 4.1 Hiển thị tiêu đề
    setTitleEl.innerText = dto.getTitle ? dto.getTitle() : dto.title;

    setTitleNavEl.innerText = dto.getTitle ? dto.getTitle() : dto.title;

    // 4.2 Thiết lập biến currentVisible
    currentVisible = dto.visible;

    // 4.3 Cập nhật nút toggle dựa trên currentVisible
    updateToggleButton();

    // 4.4 Clear cũ rồi fill danh sách thẻ
    cardsTbody.innerHTML = '';

    if (dto.flashcards && dto.flashcards.length > 0) {
      dto.flashcards.forEach(card => {
        // Mỗi card: { frontContent, backContent }
        const tr = document.createElement('tr');

        // Tiếng Anh
        const engTd = document.createElement('td');
        engTd.innerText = card.frontContent;
        tr.appendChild(engTd);

        // Tiếng Việt
        const viTd = document.createElement('td');
        viTd.innerText = card.backContent;
        tr.appendChild(viTd);

        cardsTbody.appendChild(tr);
      });
    } else {
      // Nếu không có thẻ nào
      cardsTbody.innerHTML = `
        <tr>
          <td colspan="2" class="text-center text-secondary">Bộ chưa có thẻ nào.</td>
        </tr>`;
    }
  }

  // 5) Hàm cập nhật label + style cho nút toggle
  function updateToggleButton() {
    if (currentVisible) {
      toggleBtn.innerText = 'Ẩn Bộ';
      toggleBtn.classList.remove('btn-outline-success');
      toggleBtn.classList.add('btn-outline-danger');
    } else {
      toggleBtn.innerText = 'Hiện Bộ';
      toggleBtn.classList.remove('btn-outline-danger');
      toggleBtn.classList.add('btn-outline-success');
    }
  }

  // 6) Hàm gọi API để thay đổi trạng thái visible
  function toggleVisibility() {
    // Xác định action (hide hoặc show)
    const action = currentVisible ? 'hide' : 'show';

    fetch(`${baseUrl}/api/admin/flashcardsets/${setId}/${action}`, {
      method: 'PUT',
      headers: authHeaders()
    })
      .then(res => {
        if (res.status === 401 || res.status === 403) {
          window.location.href = 'sign-in.html';
          throw new Error('Unauthorized');
        }
        if (!res.ok) {
          throw new Error(`HTTP ${res.status}`);
        }
        // Thành công, chỉ cần đổi trạng thái hiện tại
        currentVisible = !currentVisible;
        updateToggleButton();
      })
      .catch(err => {
        console.error('Lỗi khi đổi trạng thái:', err);
        alert('Không thể thay đổi trạng thái. Vui lòng thử lại.');
      });
  }

  // 7) Gắn event click cho nút toggle
  toggleBtn.addEventListener('click', () => {
    toggleVisibility();
  });

  // 8) Gọi lần đầu để load chi tiết
  fetchSetDetail();
});
