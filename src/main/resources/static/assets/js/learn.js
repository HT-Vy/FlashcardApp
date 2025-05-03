;(async function(){
    const token = sessionStorage.getItem('token');
    if (!token) return window.location.href = 'sign-in.html';
    const headers = { 'Authorization': 'Bearer ' + token };
  
    // Lấy setId
    const params = new URLSearchParams(window.location.search);
    const setId = params.get('setId');
    if (!setId) return alert('Không xác định được bộ flashcard');
  
    // Fetch dữ liệu set
    let flashcards = [];
    try {
      const res = await fetch(`/api/sets/${setId}`, { headers });
      if (!res.ok) throw new Error(res.statusText);
      const data = await res.json();
      document.getElementById('setTitle').innerText = data.title;
      flashcards = data.flashcards;
    } catch (e) {
      console.error(e);
      return alert('Lỗi tải dữ liệu bộ flashcard');
    }
  
    // **SỬA Ở ĐÂY**: map đúng trường frontContent/backContent
    const words = flashcards.map(f => ({
      en: f.frontContent,
      vn: f.backContent
    }));
    let currentIndex = 0;
  
    // Build table vocab
    const tbody = document.querySelector('.vocab-table tbody');
    tbody.innerHTML = words.map(w => `
      <tr><td>${w.en}</td><td>${w.vn}</td></tr>
    `).join('');
    const cardEl = document.getElementById('card');
    // Hiển thị card đầu
    function updateCardDisplay() {
        // mỗi lần đổi từ mới, đảm bảo reset về mặt trước
        cardEl.classList.remove('flipped');
      const front = document.getElementById('front');
      const back  = document.getElementById('back');
      const mode  = document.getElementById('displayMode').value;
      const w     = words[currentIndex];
  
      if (mode === 'VN') {
        front.textContent = '?';
        back.textContent  = w.vn;
      } else if (mode === 'EN') {
        front.textContent = w.en;
        back.textContent  = '?';
      } else {
        front.textContent = w.en;
        back.textContent  = w.vn;
      }
  
      // Ẩn card khi đổi từ mới
    //   document.getElementById('card').classList.add('hidden');
      document.getElementById('card-position')
              .textContent = `${currentIndex+1} / ${words.length}`;
    }
  
    function nextWord() {
      currentIndex = (currentIndex + 1) % words.length;
      updateCardDisplay();
      speakWord();
    }
    function previousWord() {
      currentIndex = (currentIndex - 1 + words.length) % words.length;
      updateCardDisplay();
      speakWord();
    }
  
    // **SỬA Ở ĐÂY**: toggle bằng style hoặc class
    function toggleCard() {
      // click thì lật
        cardEl.classList.toggle('flipped');
        speakWord();
    }
  
    cardEl.addEventListener('click', toggleCard);
    document.querySelector('[onclick="nextWord()"]')
            .addEventListener('click', nextWord);
    document.querySelector('[onclick="previousWord()"]')
            .addEventListener('click', previousWord);
            
    function speakWord() {
      const voice = document.getElementById('voiceMode').value;
      if (voice === 'None') return;
      const u = new SpeechSynthesisUtterance(words[currentIndex].en);
      u.lang = voice;
      speechSynthesis.speak(u);
    }
  
    // Toggle settings panel
    function toggleSettings() {
      const s = document.getElementById('settings');
      s.style.display = s.style.display === 'none' ? 'block' : 'none';
    }
  
    // Gán sự kiện
    document.querySelector('[onclick="nextWord()"]')
            .addEventListener('click', nextWord);
    document.querySelector('[onclick="previousWord()"]')
            .addEventListener('click', previousWord);
    document.getElementById('card')
            .addEventListener('click', toggleCard);
    document.getElementById('displayMode')
            .addEventListener('change', updateCardDisplay);
    document.querySelector('.settings-icon')
            .addEventListener('click', toggleSettings);
  
    // Bookmark (giữ nguyên)
    const bookmarkBtn = document.getElementById('bookmarkBtn');
    const icon = bookmarkBtn.querySelector('i');
    bookmarkBtn.addEventListener('click', ()=>{
      bookmarkBtn.classList.toggle('active');
      icon.classList.toggle('far');
      icon.classList.toggle('fas');
      // TODO: gọi API lưu/bỏ lưu
    });
  
    // Chạy lần đầu
    updateCardDisplay();
  
    // Scrollbar (giữ nguyên)
    if (navigator.platform.indexOf('Win') > -1 && document.querySelector('#sidenav-scrollbar')) {
      Scrollbar.init(document.querySelector('#sidenav-scrollbar'), { damping: 0.5 });
    }
  
    // expose các hàm inline nếu cần
    window.nextWord       = nextWord;
    window.previousWord   = previousWord;
    window.toggleCard     = toggleCard;
    window.toggleSettings = toggleSettings;
  })();
  