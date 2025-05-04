;(function(){
    const params = new URLSearchParams(location.search);
    const setId = params.get("setId");
    const token = sessionStorage.getItem("token");
    const headers = {
      "Authorization": "Bearer "+token,
      "Content-Type": "application/json"
    };
  
    // … các biến DOM giữ nguyên …
  
    startBtn.onclick = () => {
      const mode = document.getElementById("modePractice").checked ? "review":"test";
      fetch(`/api/sets/${setId}/quiz/${mode}`, { headers })
        .then(r=>r.json())
        .then(data => {
          quizData = data; idx=0; score=0;
          modeScreen.style.display="none";
          quizContent.style.display="block";
          renderCard();
        });
    };
  
    function checkAnswer(){
      const card = quizData[idx];
      fetch(`/api/sets/${setId}/quiz/${mode}/evaluate`, {
        method:"POST", headers,
        body: JSON.stringify({
          flashcardId: card.id,
          userAnswer: answerInput.value
        })
      })
      .then(r=>r.json())
      .then(res => {
        /* thêm class is-valid/is-invalid */ 
        // … sau đó next hoặc show kết quả …
      });
    }
    // gán nextBtn.onclick = checkAnswer; v.v.
  })();
  