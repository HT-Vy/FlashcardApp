// src/main/resources/static/assets/js/navbar.js
(async function(){
    const token = sessionStorage.getItem('token');
    if (!token) return; // chưa login thì không làm gì
  
    try {
      const res = await fetch('/api/auth/me', {
        headers: { 'Authorization': 'Bearer ' + token }
      });
      if (!res.ok) throw new Error('Unauthorized');
      const user = await res.json();
  
      // Gán avatar URL (hoặc để mặc định nếu null)
      const avatarEl = document.getElementById('navbarAvatar');
      if (avatarEl && user.avatarUrl) {
        avatarEl.src = user.avatarUrl;
      }
    } catch (err) {
      console.error('Cannot load navbar avatar:', err);
      // giữ nguyên avatar mặc định nếu lỗi
    }
  })();

  document.addEventListener('DOMContentLoaded', () => {
    const btn = document.getElementById('logoutBtn');
    if (!btn) return;
  
    btn.addEventListener('click', () => {
      // Xóa token khỏi sessionStorage
      sessionStorage.removeItem('token');
      // Quay về trang đăng nhập
      window.location.href = 'sign-in.html';
    });
  });
  
  