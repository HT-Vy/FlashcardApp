// src/main/resources/static/assets/js/admin.js

const baseUrl = '';  // nếu front + back cùng host thì để trống

document.addEventListener('DOMContentLoaded', () => {
  const token = sessionStorage.getItem('token');
  if (!token) {
    // nếu chưa login thì return luôn
    window.location.href = 'sign-in.html';
    return;
  }

  const tbody = document.getElementById('setsTbody');

  // helper để build headers
  const authHeaders = () => ({
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  });

  // 1) Fetch toàn bộ flashcardset
  fetch(`${baseUrl}/api/admin/flashcardsets`, {
    method: 'GET',
    headers: authHeaders()
  })
    .then(res => {
      if (res.status === 401 || res.status === 403) {
        // token hết hạn hoặc ko phải ADMIN
        window.location.href = 'sign-in.html';
        return;
      }
      if (!res.ok) {
        throw new Error(`HTTP ${res.status}`);
      }
      return res.json();
    })
    .then(sets => {
      if (!sets) return; // nếu đã redirect
      tbody.innerHTML = sets.map(fs => `
        <tr data-id="${fs.id}">
          <td class="ps-4">
            <span class="text-dark font-weight-bold">${fs.title}</span>
          </td>
          <td class="text-center">
            ${fs.savedCount}
          </td>
          <td class="text-center">
            ${fs.cardCount}
          </td>
          <td class="text-center">
            ${fs.ownerName}
          </td>
          <td class="text-center">
            <p class="text-sm text-secondary mb-3 ms-auto">
                        ${fs.avg.toFixed(1)} 
                        <i class="fas fa-star text-warning me-1" style="font-size: 1rem"></i>
                </p>
          </td>
          <td class="text-center">
            <button
              class="btn btn-sm toggle-btn ${fs.visible ? 'btn-success' : 'btn-secondary'}"
              data-visible="${fs.visible}">
              ${fs.visible ? 'Ẩn' : 'Hiện'}
            </button>
          </td>
        </tr>
      `).join('');
    })
    .catch(err => {
      console.error('Lỗi khi load admin sets:', err);
      tbody.innerHTML = `
        <tr>
          <td colspan="5" class="text-center text-danger">
            Không tải được dữ liệu.
          </td>
        </tr>`;
    });

  // 2) Xử lý click lên nút toggle (Hide / Show)
  tbody.addEventListener('click', e => {
    const btn = e.target.closest('.toggle-btn');
    if (!btn) return;

    const tr = btn.closest('tr');
    const id = tr.dataset.id;
    const currentlyVisible = btn.dataset.visible === 'true';
    const action = currentlyVisible ? 'hide' : 'show';

    fetch(`${baseUrl}/api/admin/flashcardsets/${id}/${action}`, {
      method: 'PUT',
      headers: authHeaders()
    })
      .then(res => {
        if (res.status === 401 || res.status === 403) {
          window.location.href = 'sign-in.html';
          return;
        }
        if (!res.ok) {
          throw new Error(`HTTP ${res.status}`);
        }
        // cập nhật UI tại chỗ
        const newVisible = !currentlyVisible;
        btn.dataset.visible = newVisible;
        btn.classList.toggle('btn-success', newVisible);
        btn.classList.toggle('btn-secondary', !newVisible);
        btn.textContent = newVisible ? 'Ẩn' : 'Hiện';
      })
      .catch(err => {
        console.error('Lỗi khi toggle visible:', err);
        alert('Không thể thay đổi trạng thái!');
      });
  });
});
