// // ĐĂNG NHẬP
// // Chờ khi toàn bộ DOM đã load
// document.addEventListener("DOMContentLoaded", function () {
//     // Lấy form login
//     const loginForm = document.getElementById("loginForm");
  
//     // Gắn sự kiện khi submit form
//     if (loginForm) {
//       loginForm.addEventListener("submit", async function (event) {
//         event.preventDefault(); // Ngăn reload trang
  
//         // Lấy dữ liệu từ input
//         const email = document.getElementById("email").value;
//         const password = document.getElementById("password").value;
  
//         try {
//           // Gửi request POST tới backend login API
//           const response = await fetch("/api/auth/login", {
//             method: "POST",
//             headers: {
//               "Content-Type": "application/json"
//             },
//             body: JSON.stringify({
//               email: email,
//               password: password
//             })
//           });
  
//           // Nếu đăng nhập thành công
//           if (response.ok) {
//             const data = await response.json();
//             const token = data.token;
  
//             // Lưu token vào localStorage để dùng sau
//             localStorage.setItem("token", token);
  
//             // Chuyển hướng sang trang chính
//             window.location.href = "/pages/dashboard.html";

//           } else {
//             // Hiển thị lỗi nếu có
//             const error = await response.text();
//             alert("Đăng nhập thất bại: " + error);
//           }
//         } catch (error) {
//           console.error("Lỗi khi gửi request:", error);
//           alert("Lỗi kết nối server!");
//         }
//       });
//     }
//   });


// // ĐĂNG KÝ
// // Xử lý khi form đăng ký được gửi
// document.getElementById("registerForm").addEventListener("submit", function(e) {
//     e.preventDefault(); // Ngừng hành động mặc định của form

//     // Lấy giá trị từ các trường trong form
//     const email = document.getElementById("email").value;
//     const fullname = document.getElementById("fullname").value;
//     const password = document.getElementById("password").value;

//     // Gửi yêu cầu đăng ký đến backend
//     fetch("/api/auth/register", {
//       method: "POST", // Phương thức POST
//       headers: {
//         "Content-Type": "application/json" // Định dạng dữ liệu gửi đi là JSON
//       },
//       body: JSON.stringify({
//         email: email, // Gửi email
//         fullName: fullname, // Gửi họ tên người dùng (fullName)
//         password: password // Gửi mật khẩu
//       })
//     })
//     .then(response => response.json()) // Xử lý phản hồi từ backend
//     .then(data => {
//       if (data.email) { // Nếu đăng ký thành công và có trả về thông tin người dùng
//         alert("Đăng ký thành công! Bạn có thể đăng nhập ngay.");
//         window.location.href = "/pages/sign-in.html"; // Chuyển hướng đến trang đăng nhập
//       } else {
//         alert("Có lỗi xảy ra: " + data); // Nếu có lỗi, hiển thị thông báo lỗi
//       }
//     })
//     .catch(error => {
//       console.error("Có lỗi xảy ra:", error);
//       alert("Có lỗi xảy ra trong quá trình đăng ký.");
//     });
//   });
  // Đợi khi toàn bộ DOM được load
document.addEventListener("DOMContentLoaded", function () {
    // ===============================
    // Xử lý đăng nhập
    // ===============================

    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", async function (event) {
            event.preventDefault(); // Ngăn reload trang

            // Lấy dữ liệu từ input
            const email = document.getElementById("email").value.trim();
            const password = document.getElementById("password").value.trim();

            // Kiểm tra dữ liệu
            if (!email || !password) {
                alert("Vui lòng nhập đầy đủ Email và Mật khẩu!");
                return;
            }

            try {
                const response = await fetch("/api/auth/login", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ email, password })
                });

                const data = await response.text(); // Lấy dữ liệu trả về dưới dạng text

                if (response.ok) {
                    const { token } = JSON.parse(data); // Parse JSON nếu login thành công
                    localStorage.setItem("token", token); // Lưu token
                    window.location.href = "/pages/dashboard.html"; // Chuyển trang
                } else {
                    alert("Đăng nhập thất bại: " + data);
                }
            } catch (error) {
                console.error("Lỗi khi đăng nhập:", error);
                alert("Không thể kết nối tới server!");
            }
        });
    }

    // ===============================
    // Xử lý đăng ký
    // ===============================

    const registerForm = document.getElementById("registerForm");
    if (registerForm) {
        registerForm.addEventListener("submit", async function (e) {
            e.preventDefault(); // Ngăn reload trang

            // Lấy dữ liệu từ form
            const email = document.getElementById("email").value.trim();
            const fullName = document.getElementById("fullName").value.trim();
            const password = document.getElementById("password").value.trim();

            // Validate dữ liệu
            if (!email || !fullName || !password) {
                alert("Vui lòng nhập đầy đủ Email, Họ tên và Mật khẩu!");
                return;
            }

            try {
                const response = await fetch("/api/auth/register", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ email, fullName, password })
                });

                const data = await response.text(); // Lấy dữ liệu trả về dưới dạng text

                if (response.ok) {
                    const user = JSON.parse(data); // Parse JSON nếu đăng ký thành công
                    alert("Đăng ký thành công! Bạn có thể đăng nhập ngay.");
                    window.location.href = "/pages/sign-in.html"; // Chuyển hướng trang
                } else {
                    alert("Đăng ký thất bại: " + data); // Hiển thị lỗi từ server
                }
            } catch (error) {
                console.error("Lỗi khi đăng ký:", error);
                alert("Không thể kết nối tới server!");
            }
        });
    }
});
