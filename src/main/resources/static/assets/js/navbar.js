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

  // Hàm xoá token và chuyển về trang đăng nhập
function logout() {
  // 1. Xoá token
  sessionStorage.removeItem('token');
  // 2. Chuyển về sign-in.html
  window.location.href = 'sign-in.html';
}

// Gắn event cho thẻ <a>
document.addEventListener('DOMContentLoaded', function() {
  const logoutLink = document.getElementById('logoutLink');
  if (logoutLink) {
    logoutLink.addEventListener('click', function(e) {
      e.preventDefault();  // không để <a> nhảy đường dẫn mặc định
      //logout ở client:
      logout();
    });
  }
});
  
  