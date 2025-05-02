// ĐỊnh nghĩa các hàm fetch dữ liệu
const token = sessionStorage.getItem('token');
const headers = { 'Authorization': 'Bearer ' + token };

async function fetchMySets() {
  // Đọc từ /api/auth/me thì trong flashcardSets bạn đã có bộ tự tạo
  const res = await fetch('/api/auth/me', { headers });
  if (!res.ok) throw new Error('Không thể lấy bộ của bạn');
  const user = await res.json();
  return user.flashcardSets;
}

async function fetchSavedSets() {
  // endpoint bạn toggle/get user collections: GET /api/collections
  const res = await fetch('/api/collections', { headers });
  if (!res.ok) throw new Error('Không thể lấy bộ đã lưu');
  return await res.json(); // giả sử trả List<FlashcardSetDTO>
}

async function fetchAllSets() {
  // Lấy cả 2: tự tạo + đã lưu, gộp lại
  const [mine, saved] = await Promise.all([fetchMySets(), fetchSavedSets()]);
  // Đảm bảo không duplicate nếu user lưu chính bộ của mình
  const map = new Map();
  mine.forEach(s => map.set(s.id, s));
  saved.forEach(s => { if (!map.has(s.id)) map.set(s.id, s); });
  return Array.from(map.values());
}

// hàm render bảng
function renderTable(sets) {
    const tbody = document.getElementById('collectionTableBody');
    tbody.innerHTML = sets.map(s => `
      <tr>
        <td style="padding: 18px">
          <a href="./learn.html?setId=${s.id}" class="text-decoration-none">
            <h6 class="mb-0 text-base">${s.title}</h6>
          </a>
        </td>
        <td><p class="mb-0">${s.savedByCount}</p></td>
        <td><p class="mb-0">${new Date(s.lastStudiedAt || s.createdAt).toLocaleString()}</p></td>
        <td>
          <div class="d-flex align-items-center">
            <img src="${s.ownerAvatarUrl || '../assets/img/avatar.png'}"
                 class="avatar avatar-xs rounded-circle me-2" alt="avt tác giả">
            <span>${s.ownerName}</span>
          </div>
        </td>
      </tr>
    `).join('');
  }
// Gắn event cho 3 radio và khởi chạy ban đầu

document.addEventListener('DOMContentLoaded', () => {
    const rAll   = document.getElementById('btnradiotable1');
    const rMine  = document.getElementById('btnradiotable2');
    const rSaved = document.getElementById('btnradiotable3');
  
    // Helper để set loading state
    async function loadAndRender(loader) {
      try {
        const sets = await loader();
        renderTable(sets);
      } catch (e) {
        console.error(e);
        alert(e.message);
      }
    }
  
    // Khi click Tất cả
    rAll.addEventListener('change', () => {
      if (!rAll.checked) return;
      loadAndRender(fetchAllSets);
    });
  
    // Khi click Của tôi
    rMine.addEventListener('change', () => {
      if (!rMine.checked) return;
      loadAndRender(fetchMySets);
    });
  
    // Khi click Đã lưu
    rSaved.addEventListener('change', () => {
      if (!rSaved.checked) return;
      loadAndRender(fetchSavedSets);
    });
  
    // Ban đầu load Tất cả
    loadAndRender(fetchAllSets);
  });
  