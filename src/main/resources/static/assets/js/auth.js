// src/main/resources/static/assets/js/auth.js

// Base URL của backend (để trống nếu cùng host)
const baseUrl = '';

// ======== Đăng ký (Register) ========
const registerForm = document.getElementById('registerForm');
if (registerForm) {
  // Lấy các phần tử cần thiết
  const fullNameInput = document.getElementById('fullName');
  const emailInput = document.getElementById('email');
  const passwordInput = document.getElementById('password');
  const submitBtn = registerForm.querySelector('button[type="submit"]');
  const errDiv = document.getElementById('registerError');

  // Xóa lỗi khi user nhập lại
  [fullNameInput, emailInput, passwordInput].forEach(input => {
    input.addEventListener('input', () => {
      errDiv.innerText = '';
    });
  });

  registerForm.addEventListener('submit', async e => {
    e.preventDefault();
    errDiv.innerText = '';

    const fullName = fullNameInput.value.trim();
    const email = emailInput.value.trim();
    const password = passwordInput.value;

    // Client-side validation
    if (!fullName) {
      errDiv.innerText = 'Họ và tên không được để trống';
      return;
    }
    if (!/^\S+@\S+\.\S+$/.test(email)) {
      errDiv.innerText = 'Email không hợp lệ';
      return;
    }
    if (password.length < 6) {
      errDiv.innerText = 'Mật khẩu phải có ít nhất 6 ký tự';
      return;
    }

    // Disable button + hiện spinner
    submitBtn.disabled = true;
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang xử lý';

    try {
      const res = await fetch(`${baseUrl}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fullName, email, password })
      });
      if (!res.ok) {
        const errorBody = await res.json();
        throw new Error(errorBody.message || 'Đăng ký thất bại');
      }
      // Thành công → chuyển sang trang đăng nhập
      window.location.href = 'sign-in.html';
    } catch (err) {
      errDiv.innerText = err.message;
    } finally {
      // Reset button state
      submitBtn.disabled = false;
      submitBtn.innerHTML = originalText;
    }
  });
}

// ======== Đăng nhập (Login) ========
const loginForm = document.getElementById('loginForm');
if (loginForm) {
  const emailInput = document.getElementById('email');
  const passwordInput = document.getElementById('password');
  const submitBtn = loginForm.querySelector('button[type="submit"]');
  const errDiv = document.getElementById('loginError');

  // Xóa lỗi khi nhập lại
  [emailInput, passwordInput].forEach(input => {
    input.addEventListener('input', () => {
      errDiv.innerText = '';
    });
  });

  loginForm.addEventListener('submit', async e => {
    e.preventDefault();
    errDiv.innerText = '';

    const email = emailInput.value.trim();
    const password = passwordInput.value;

    // Client-side validation
    if (!/^\S+@\S+\.\S+$/.test(email)) {
      errDiv.innerText = 'Email không hợp lệ';
      return;
    }
    if (!password) {
      errDiv.innerText = 'Mật khẩu không được để trống';
      return;
    }

    // Disable button + spinner
    submitBtn.disabled = true;
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang xử lý';

    try {
      const res = await fetch(`${baseUrl}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });
      if (!res.ok) {
        const errorBody = await res.json();
        throw new Error(errorBody.message || 'Đăng nhập thất bại');
      }

      const { token, role } = await res.json();
      // Lưu token
      sessionStorage.setItem('token', token);
      // Redirect
      // Redirect theo role
      if (role === 'ADMIN') {
        window.location.href = 'admindashboard.html';      // admin
      } else {
        window.location.href = 'dashboard.html';  // trang user bình thường
      }
    } catch (err) {
      errDiv.innerText = err.message;
    } finally {
      submitBtn.disabled = false;
      submitBtn.innerHTML = originalText;
    }
  });
}
