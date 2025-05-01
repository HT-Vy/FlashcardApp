// src/main/resources/static/assets/js/profile.js
console.log('✅ profile.js loaded');

(async function(){
    const token = sessionStorage.getItem('token');
    if (!token) {
      return window.location.href = 'sign-in.html';
    }
    const headers = { 'Authorization': 'Bearer ' + token };
  
    // 1) Lấy profile user
    let user;
    try {
      const res = await fetch('/api/auth/me', { headers });
      user = await res.json();
      console.log('👤 user:', user);
      console.log('🃏 flashcardSets:', user.flashcardSets);

      document.getElementById('profileName').innerText      = user.fullName;
      document.getElementById('profileFullName').innerText  = user.fullName;
      document.getElementById('profileEmail').innerText     = user.email;
      document.getElementById('profileEmailDetail').innerText = user.email;
      if (user.avatarUrl) {
        document.getElementById('profileAvatar').src = user.avatarUrl;
      }
    } catch (err) {
      console.error(err);
      alert('Lỗi tải thông tin cá nhân');
    }
  
    // 2) Upload avatar
      const uploadBtn = document.getElementById('uploadAvatarBtn');
      if(uploadBtn){
        uploadBtn.addEventListener('click', async () => {
          if (!fileInput.files.length) {
            return alert('Vui lòng chọn ảnh trước khi tải lên');
          }
          const formData = new FormData();
          formData.append('avatar', fileInput.files[0]);
      
          try {
            const uploadRes = await fetch(`/api/auth/user/${user.id}/avatar`, {
              method: 'POST',
              headers: { 'Authorization': 'Bearer ' + token },
              body: formData
            });
            if (!uploadRes.ok) {
              const msg = await uploadRes.text();
              throw new Error(msg || 'Lỗi upload');
            }
            const { avatarUrl } = await uploadRes.json();
            document.getElementById('profileAvatar').src = avatarUrl;
            alert('Cập nhật ảnh thành công');
          } catch (err) {
            console.error(err);
            alert('Lỗi cập nhật ảnh: ' + err.message);
          }
        });
      }
      
  
    // 3) Hiển thị bộ flashcard do user tạo
    try {
      // Giả sử user.flashcardSets là mảng bộ do user tạo
      const container = document.getElementById('userSetsRow');
      console.log('Container:', container);

      container.innerHTML = user.flashcardSets.map(s => `
              <div class="col-xl-3 col-sm-6 mb-xl-0">
                <div class="card border shadow-xs mb-4">
                  <a href="./learn.html?setId=${s.id}" class="text-decoration-none">
                    <div class="card-body p-3">
                      <p class="text-sm text-secondary mb-1">${s.flashcardCount} thẻ</p>
                      <h6 class="font-weight-bold mb-2">${s.title}</h6>
                    </div>
                  </a>
                </div>
              </div>
            `).join('');
          } catch (err) {
            console.error('Lỗi tải bộ flashcard', err);
          }
  
    // 4) Cancel / Save (nếu có)
    document.getElementById('cancelProfile').addEventListener('click', () => window.location.reload());
    document.getElementById('saveProfile').addEventListener('click', () => {
      // thêm logic nếu bạn cho phép edit fullName/email
      alert('Chức năng lưu thông tin cá nhân chưa hoàn thiện');
    });
  })();
  