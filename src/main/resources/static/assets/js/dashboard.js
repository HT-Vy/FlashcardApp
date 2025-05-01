// src/main/resources/static/assets/js/dashboard.js
// Dynamic rendering of Dashboard while preserving existing CSS styling

(async function(){
    const token = sessionStorage.getItem('token');
    if (!token) {
      window.location.href = 'sign-in.html';
      return;
    }
    const headers = { 'Authorization': 'Bearer ' + token };
  
    // 1) Greeting
    try {
      const meRes = await fetch('/api/dashboard/me', { headers });
      const name = await meRes.text();
      document.getElementById('greeting').innerText = 'Xin Chào, ' + name;
    } catch (err) {
      console.error('Error fetching greeting:', err);
    }
  
    // 2) Recent study cards
    try {
      const recentRes = await fetch('/api/dashboard/recent', { headers });
      const recentSets = await recentRes.json();
      const recentRow = document.getElementById('recentSetsRow');
      recentRow.innerHTML = recentSets.map(s => `
        <div class="col-xl-3 col-sm-6 mb-xl-0">
          <div class="card border shadow-xs mb-4">
            <a href="./learn.html?setId=${s.id}" style="text-decoration:none;">
              <div class="card-body text-start p-3 w-100">
                <div
                  class="icon icon-shape icon-sm bg-dark text-white text-center border-radius-sm d-flex align-items-center justify-content-center mb-3">
                  <svg height="16" width="16" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
                    fill="currentColor">
                    <path d="M4.5 3.75a3 3 0 00-3 3v.75h21v-.75a3 3 0 00-3-3h-15z"></path>
                    <path fill-rule="evenodd"
                      d="M22.5 9.75h-21v7.5a3 3 0 003 3h15a3 3 0 003-3v-7.5zm-18 3.75a.75.75 0 01.75-.75h6a.75.75 0 010 1.5h-6a.75.75 0 01-.75-.75zm.75 2.25a.75.75 0 000 1.5h3a.75.75 0 000-1.5h-3z"
                      clip-rule="evenodd"></path>
                  </svg>
                </div>
                <div class="row">
                  <div class="col-12">
                    <div class="w-100">
                      <p class="text-sm text-secondary mb-1">${s.flashcardCount} thẻ</p>
                      <h4 class="mb-2 font-weight-bold text-truncate custom-title">${s.title}</h4>
                      <div class="d-flex align-items-center">
                        <span class="text-sm text-success font-weight-bolder">
                          <i class="fa fa-chevron-up text-xs me-1"></i> ${s.progressPercent.toFixed(1)}%
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </a>
          </div>
        </div>
      `).join('');
    } catch (err) {
      console.error('Error fetching recent sets:', err);
    }
  
    // 3) Top popular table
    try {
      const popRes = await fetch('/api/dashboard/popular', { headers });
      const popular = await popRes.json();
      const tbody = document.getElementById('popularTbody');
      tbody.innerHTML = popular.map(s => `
        <tr>
          <td>
            <a href="./learn.html?setId=${s.id}" style="text-decoration:none;">
              <div class="d-flex px-2">
                <div class="my-auto">
                  <h6 class="mb-0 text-base">${s.title}</h6>
                </div>
              </div>
            </a>
          </td>
          <td>
            <p class="text-base font-weight-normal mb-0">${s.savedByCount}</p>
          </td>
          <td>
            <span class="text-base font-weight-normal">${new Date(s.lastStudiedAt).toLocaleString()}</span>
          </td>
          <td class="align-middle">
            <div class="d-flex">
              <div class="d-flex align-items-center">
                <img src="../assets/img/user-avatar.png" class="avatar avatar-sm rounded-circle me-2" alt="user">
              </div>
              <div class="ms-2">
                <a href="./profile.html" class="text-dark text-base mb-0">${s.ownerName}</a>
              </div>
            </div>
          </td>
        </tr>
      `).join('');
    } catch (err) {
      console.error('Error fetching popular sets:', err);
    }
  })();
  