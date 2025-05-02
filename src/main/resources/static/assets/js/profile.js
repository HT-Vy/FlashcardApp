console.log('✅ profile.js loaded');

(async function(){
  const token = sessionStorage.getItem('token');
  if (!token) {
    return window.location.href = 'sign-in.html';
  }
  const headers = { 'Authorization': 'Bearer ' + token };

  let user;
  try {
    // 1) Lấy profile user
    const res = await fetch('/api/auth/me', { headers });
    if (!res.ok) throw new Error('Không lấy được thông tin user');
    user = await res.json();
    console.log('📦 profile user object:', user);

    // Hiển thị lên page
    document.getElementById('profileName').innerText      = user.fullName;
    document.getElementById('profileFullName').innerText  = user.fullName;
    document.getElementById('profileEmail').innerText     = user.email;
    document.getElementById('profileEmailDetail').innerText = user.email;
    if (user.avatarUrl) {
      document.getElementById('profileAvatar').src = user.avatarUrl;
    }

    // 2) Chỉ bind các event sau khi đã có user
    const avatarInput   = document.getElementById('avatarInput');
    const fullnameInput = document.getElementById('fullnameInput');

    // Mở modal: điền sẵn tên và clear file
    document.getElementById('editProfileBtn')
      .addEventListener('click', () => {
        fullnameInput.value = user.fullName || '';
        avatarInput.value  = '';
      });

    // Cancel: đóng modal
    document.getElementById('cancelProfile')
      .addEventListener('click', () => {
        bootstrap.Modal.getInstance(
          document.getElementById('editProfileModal')
        ).hide();
      });

    // Save: hẳn chạy khi user đã có id
    document.getElementById('saveProfile')
      .addEventListener('click', async () => {
        console.log('PUT to /api/auth/user/' + user.id);
        const formData = new FormData();
        formData.append('fullName', fullnameInput.value);
        if (avatarInput.files.length) {
          formData.append('avatar', avatarInput.files[0]);
        }

        try {
          const putRes = await fetch(`/api/auth/user/${user.id}`, {
            method: 'PUT',
            headers: { 'Authorization': 'Bearer ' + token },
            body: formData
          });
          if (!putRes.ok) throw new Error(await putRes.text());
          const updated = await putRes.json();

          // Cập nhật UI
          document.getElementById('profileName').innerText      = updated.fullName;
          document.getElementById('profileFullName').innerText  = updated.fullName;
          if (updated.avatarUrl) {
            document.getElementById('profileAvatar').src = updated.avatarUrl;
          }

          // Đóng modal
          bootstrap.Modal.getInstance(
            document.getElementById('editProfileModal')
          ).hide();
          alert('Cập nhật hồ sơ thành công!');
          user = updated; // cập nhật lại user object
        } catch (e) {
          console.error(e);
          alert('Lỗi lưu hồ sơ: ' + e.message);
        }
      });

    // 3) Hiển thị các bộ flashcard
    const container = document.getElementById('userSetsRow');
    container.innerHTML = user.flashcardSets.map(s => `
      <div class="col-xl-3 col-sm-6 mb-xl-0">
        <div class="card border shadow-xs mb-4">
          <a href="./learn.html?setId=${s.id}" class="text-decoration-none">
            <div class="card-body p-3">
              <p class="text-sm text-secondary mb-1">${s.flashcardCount || 0} thẻ</p>
              <h6 class="font-weight-bold mb-2">${s.title}</h6>
            </div>
          </a>
        </div>
      </div>
    `).join('');

  } catch (err) {
    console.error(err);
    alert('Lỗi tải thông tin cá nhân: ' + err.message);
    // Không bind tiếp nếu không có user
  }
})();
