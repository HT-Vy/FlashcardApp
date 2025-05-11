// flashcardset.js
; (async function () {
  const token = sessionStorage.getItem('token') || localStorage.getItem('token');
  if (!token) return window.location.href = 'sign-in.html';

  const headers = {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  };

  // DOM refs
  const titleEl = document.getElementById('title');
  const descEl = document.getElementById('description');
  // const publicToggle  = document.getElementById('public');
  const cardListEl = document.getElementById('card-list');
  const submitBtn = document.querySelector('.submit-btn');
  const addCardBtn = document.querySelector('.add-card-btn');

  // helper: tạo mới một card (hoặc với front/back có sẵn)
  let cardCount = 0;
  window.addCard = function (front = '', back = '') {
    cardCount++;
    const card = document.createElement('div');
    card.className = 'card';
    card.setAttribute('data-index', cardCount);
    card.innerHTML = `
        <div class="card-number">${cardCount}</div>
        <div class="term-row">
          <input type="text" placeholder="THUẬT NGỮ" value="${front}"/>
          <input type="text" placeholder="ĐỊNH NGHĨA" value="${back}"/>
        </div>
        <button class="delete-button">🗑</button>
      `;
    // delete
    card.querySelector('.delete-button').addEventListener('click', () => {
      card.remove();
      // re-index
      Array.from(cardListEl.children).forEach((c, i) => {
        c.querySelector('.card-number').textContent = i + 1;
        c.setAttribute('data-index', i + 1);
      });
      cardCount = cardListEl.children.length;
    });
    cardListEl.append(card);
  };

  addCardBtn.addEventListener('click', () => addCard());

  // 1. Kiểm xem URL có ?setId= không → nếu có: edit mode
  const params = new URLSearchParams(window.location.search);
  const setId = params.get('setId');
  if (setId) {
    // đang edit: đổi chữ nút
    submitBtn.textContent = 'Lưu chỉnh sửa';

    // fetch chi tiết và populate form
    const res = await fetch(`/api/sets/${setId}`, { headers });
    if (!res.ok) return alert('Không lấy được dữ liệu set');
    const data = await res.json();

    // chú ý: nếu DTO của bạn có trường public thì đọc data.public, 
    // nếu khác tên (vd isPublic) hãy đổi lại cho đúng
    //   publicToggle.checked  = data.public; 
    titleEl.value = data.title;
    descEl.value = data.description;

    // xóa 3 card mặc định lúc load
    cardListEl.innerHTML = '';
    cardCount = 0;
    // populate cards từ backend
    data.flashcards.forEach(f => addCard(f.frontContent, f.backContent));
  } else {
    // đang tạo mới: giữ chữ mặc định (Tạo) hoặc gán lại cho rõ
    submitBtn.textContent = 'Tạo';
    // tạo mới: cho hiển thị 3 card khởi tạo sẵn
    addCard(); addCard(); addCard();
  }

  // 2. Bắt sự kiện submit
  submitBtn.addEventListener('click', async () => {
    // thu thập dữ liệu
    const payloadSet = {
      title: titleEl.value.trim(),
      description: descEl.value.trim(),
      // public:      publicToggle.checked  // nhớ DTO/controller phải hỗ trợ
    };
    // validate
    if (!payloadSet.title) {
      return alert('Vui lòng nhập tiêu đề');
    }
    // thu thập flashcards
    const cards = Array.from(cardListEl.children).map(c => {
      const [inpFront, inpBack] = c.querySelectorAll('input');
      return {
        frontContent: inpFront.value.trim(),
        backContent: inpBack.value.trim()
      };
    });
    if (cards.length === 0) {
      return alert('Phải có ít nhất 1 thẻ');
    }

    let targetSetId = setId;
    try {
      // a) Tạo mới hoặc cập nhật set
      let res;
      if (setId) {
        res = await fetch(`/api/sets/${setId}`, {
          method: 'PUT',
          headers,
          body: JSON.stringify(payloadSet)
        });
      } else {
        res = await fetch(`/api/sets`, {
          method: 'POST',
          headers,
          body: JSON.stringify(payloadSet)
        });
      }
      if (!res.ok) throw new Error(res.statusText);
      const setData = await res.json();
      targetSetId = setData.id || setId;

      // b) Gửi batch flashcards
      const batchRes = await fetch(`/api/sets/${targetSetId}/flashcards/batch`, {
        method: 'POST',
        headers,
        body: JSON.stringify({ flashcards: cards })
      });
      if (!batchRes.ok) throw new Error('Batch API lỗi');

      // c) Chuyển sang trang học
      window.location.href = `learn.html?setId=${targetSetId}`;
    } catch (e) {
      console.error(e);
      alert('Có lỗi khi lưu bộ flashcard');
    }
  });
})();
