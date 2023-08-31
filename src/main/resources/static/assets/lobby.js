var selfChoice = null;
var allowChoice = true;
var currentRound = 0;
var toNickname = null;
function onChatMessageClick() {
  var el = document.getElementById('chat-message-input');
  if (!el.value) {
    return false;
  }
  fetch('/chat/add-message', {
      method: 'POST',
      headers: {
        'Accept': 'application/json, text/plain, */*',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({gameId: 0, message: el.value, "toNickname": toNickname})
    }).then(res => res.json())
      .then(res => {
        console.log(res);
        if (res.status == 'ok')  {
          el.value = '';
        } else {
          addChatMessageError(res.errorMessage);
        }
      });
  return false;
}
/*<div class="dropdown" style="display: inline">
                            <span class="dropdown-toggle chat-message-nickname" id="dropdownMenuSpan" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            33333333333:
                            </span>
                            <div class="dropdown-menu" aria-labelledby="dropdownMenuSpan">
                                <a class="dropdown-item" href="#">Option 1</a>
                                <a class="dropdown-item" href="#">Option 2</a>
                                <a class="dropdown-item" href="#">Option 3</a>
                            </div>
                        </div>*/
function escapeHtml(html){
  var text = document.createTextNode(html);
  var p = document.createElement('p');
  p.appendChild(text);
  return p.innerHTML;
}
function getCurrentNickname(){
  var elCurrentNickname = document.getElementById('current-nickname');
  return elCurrentNickname.innerText;
}
function addChatMessage(msg) {
  var clsName = 'chat-message';
  var elMessages = document.getElementById('chat-messages');
  var chatMessage = document.createElement('div');
  var currentSender = getCurrentNickname() == msg.nickname;
  if (msg.privateMessage) {
    clsName = clsName + ' chat-messages-private';
  }
  if (currentSender) {
    clsName = clsName + ' chat-message-current-user-sender';
  }
  chatMessage.className = clsName;
  var escapeNickname = escapeHtml(msg.nickname);
  if (!currentSender) {
    chatMessage.innerHTML = `<div class="dropdown" style="display: inline">
                                   <span class="dropdown-toggle chat-message-nickname" id="dropdownMenuSpan${msg.id}"
                                    data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                    ${escapeNickname}:
                                   </span>
                                   <div class="dropdown-menu" aria-labelledby="dropdownMenuSpan${msg.id}">
                                       <a class="dropdown-item" href="#" id="menu-private-message-${msg.id}">Private message</a>
                                   </div>
                               </div>`;
  }
  var txt = document.createTextNode(msg.text);
  chatMessage.appendChild(txt);
  elMessages.insertBefore(chatMessage, elMessages.firstChild);
  if (!currentSender) {
    var menuPrivateMessage = document.getElementById('menu-private-message-' + msg.id);
    menuPrivateMessage.onclick = function() {
      onPrivateMessage(msg.nickname);
    };
  }
}
function onPrivateMessage(nickname) {
 var privateNickname = document.getElementById('nickname-span-private');
 privateNickname.innerText = nickname;
 var messagePrivate = document.getElementById('message-private');
 messagePrivate.style.display='';
 toNickname = nickname;
}
function offPrivateMessage() {
 var messagePrivate = document.getElementById('message-private');
  messagePrivate.style.display='none';
  toNickname = null;
}
function addChatMessageError(msgError) {
  var elMessages = document.getElementById('chat-messages');
  var chatMessage = document.createElement('div');
  chatMessage.className='chat-message-error';
  var txt = document.createTextNode(msgError);
  chatMessage.appendChild(txt);
  elMessages.insertBefore(chatMessage, elMessages.firstChild);
}

function onNewRound(roundNumber, isLastRound, roundsCount) {
    var elGameRunning = document.getElementById('game-body-running');
    var elCurrentRound = document.getElementById('current-round');
    var audioRound = null;
    if (roundNumber > 0 && isLastRound != true) {
        audioRound = new Audio('/assets/music/round_' + roundNumber + '.mp3');
        audioRound.play();
        elCurrentRound.innerText = 'Round ' + roundNumber + ' of ' + roundsCount;
    } else if (isLastRound == true) {
        audioRound = new Audio('/assets/music/round_last.mp3');
        audioRound.play();
        elCurrentRound.innerText = 'Last round'
    } else {
        return;
    }
    elCurrentRound.style.display='';
    var left = elGameRunning.clientWidth - elCurrentRound.clientWidth;
    if (left < 0) {
        left = 0;
    } else {
        left = Math.round(left/2);
    }
    elCurrentRound.style.left = left + 'px';
}
function onPlayerChoice(choice) {
  if(!allowChoice) {
    return;
  }
  resetSelfChoices();
  setSelfChoiceColor(choice, "_sel");
  selfChoice = choice;
}
function onSendChoice(){
if (!selfChoice) {
    return;
}
if (!allowChoice) {
    return;
}
 fetch('/game/set-round-choice', {
       method: 'POST',
       headers: {
         'Accept': 'application/json, text/plain, */*',
         'Content-Type': 'application/json'
       },
       body: JSON.stringify({choice: selfChoice})
     }).then(res => res.json())
       .then(res => {
         console.log(res);
         if (res.status == 'ok')  {

         } else {
           addChatMessageError(res.errorMessage);
         }
       });
}
function onStartGameClick(){
 fetch('/game/start-game', {
       method: 'POST',
       headers: {
         'Accept': 'application/json, text/plain, */*',
         'Content-Type': 'application/json'
       },
       body: JSON.stringify({numberOfPlayers: 2})
     }).then(res => res.json())
       .then(res => {
         console.log(res);
         if (res.status == 'ok')  {

         } else {
           addChatMessageError(res.errorMessage);
         }
       });
}
function onCancelClick(){
  fetch('/game/cancel-game', {
         method: 'POST',
         headers: {
           'Accept': 'application/json, text/plain, */*',
           'Content-Type': 'application/json'
         },
         body: JSON.stringify({})
       }).then(res => res.json())
         .then(res => {
           console.log(res);
           if (res.status == 'ok')  {

           } else {
             addChatMessageError(res.errorMessage);
           }
         });
}
function setSelfChoiceColor(choice, color) {
  var el = document.getElementById("self-" + choice);
  el.src="/assets/ic/" + choice + color + ".png";
}
function resetSelfChoices() {
  setSelfChoiceColor("rock", "")
  setSelfChoiceColor("paper", "")
  setSelfChoiceColor("scissors", "")
}
function setEnemyChoiceColor(choice, color) {
  var el = document.getElementById("enemy-" + choice);
  el.src="/assets/ic/" + choice + color + ".png";
}
function resetEnemyChoices() {
  setEnemyChoiceColor("rock", "")
  setEnemyChoiceColor("paper", "")
  setEnemyChoiceColor("scissors", "")
}
function updateGameStatus(){
fetch('/game/get-status', {
      method: 'GET',
      headers: {
        'Accept': 'application/json, text/plain, */*'
      }
    }).then(res => res.json())
      .then(res => {
         console.log('reply to query - get game status');
         console.log(res);
         var elStartGameBtn = document.getElementById('start-game-btn');
         var elCancelBtn = document.getElementById('cancel-btn');
         var elWaitingText = document.getElementById('waiting-text');
         var elGameRunning = document.getElementById('game-body-running');
         var elGameInit = document.getElementById('game-body-init');
         var gameHeaderNickname = document.getElementById('nickname');
         gameHeaderNickname.innerText = res.selfNickname;
         var gameHeaderRating = document.getElementById('rating');
         gameHeaderRating.innerText = res.selfRating;
         var elSelfScore = document.getElementById('self-score');
         var elEnemyScore = document.getElementById('enemy-score');
         var elEnemyNickname = document.getElementById('enemy-nickname');
         var elCompleted = document.getElementById('player-completed');
         var elSendChoice = document.getElementById('send-choice-btn');
         var elCurrentRound = document.getElementById('current-round');
         if (res.type == 'init'){
            elWaitingText.style.display='none';
            elStartGameBtn.style.display='';
            elCancelBtn.style.display='none';
            elGameRunning.style.display='none';
            elGameInit.style.display='';
            currentRound = 0;
         } else if (res.type == 'running') {
            var audio = null;
            elSendChoice.style.display='';
            elWaitingText.style.display='none';
            elStartGameBtn.style.display='none';
            elCancelBtn.style.display='none';
            elGameRunning.style.display='';
            elGameInit.style.display='none';
            if (!res.selfChoice) {
                if (currentRound < res.currentRound) {
                    onNewRound(res.currentRound, res.currentRound == res.roundCounts, res.roundsCount);
                    currentRound = res.currentRound;
                }
                allowChoice = true;
                resetEnemyChoices();
                resetSelfChoices();
                if (selfChoice){
                    setSelfChoiceColor(selfChoice, "_sel");
                }
            } else {
                selfChoice = null;
                resetSelfChoices();
                resetEnemyChoices();
                if (res.winner) {
                    audio = new Audio('/assets/music/round_win.mp3');
                    setSelfChoiceColor(res.selfChoice, "_win");
                    setEnemyChoiceColor(res.enemyChoice, "_lose");
                } else if (res.enemyWinner) {
                    audio = new Audio('/assets/music/round_lose.mp3');
                    setSelfChoiceColor(res.selfChoice, "_lose");
                    setEnemyChoiceColor(res.enemyChoice, "_win");
                } else {
                    setSelfChoiceColor(res.selfChoice, "_sel");
                    if (res.enemyChoice){
                        setEnemyChoiceColor(res.enemyChoice, "_sel");
                    }
                }
                allowChoice = false;
            }
            if (!allowChoice) {
                elSendChoice.style.display='none';
                elCurrentRound.style.display='none';
            }
            elSelfScore.innerText = res.selfScore;
            elEnemyScore.innerText = res.enemyScore;
            elEnemyNickname.innerText = res.enemyNickname + " (" + res.enemyRating + ")";
            if (res.currentRound == res.roundsCount && !(res.winner == null)){
                elCompleted.style.display='';
                if (res.enemyScore > res.selfScore) {
                    audio = new Audio('/assets/music/game_lose.mp3');
                    elCompleted.innerText = "Lose";
                } else if (res.enemyScore < res.selfScore){
                    audio = new Audio('/assets/music/game_win.mp3');
                    elCompleted.innerText = "Win";
                } else {
                    elCompleted.innerText = "Draw";
                }
                var left = elGameRunning.clientWidth - elCompleted.clientWidth;
                if (left < 0) {
                    left = 0;
                } else {
                    left = Math.round(left/2);
                }
                elCompleted.style.left = left + 'px';
            } else {
                elCompleted.style.display='none';
            }
            if (audio) {
                audio.play();
            }
         } else if (res.type == 'waitForGame'){
            elWaitingText.style.display='';
            elStartGameBtn.style.display='none';
            elCancelBtn.style.display='';
            elGameRunning.style.display='none';
            elGameInit.style.display='';
         }
      });
}
function loadChatMessages() {
fetch('/chat/get-messages?gameId=0', {
      method: 'GET',
      headers: {
        'Accept': 'application/json, text/plain, */*'
      }
    }).then(res => res.json())
      .then(res => {
        console.log(res);
        res.reverse().forEach((msg) => {
         addChatMessage(msg);
        });
      });
}



 const stompClient = new StompJs.Client({
    brokerURL: 'ws://' + window.location.host + '/websocket'
});

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/chat-messages/0', (chatMessage) => {
        console.log(chatMessage);
        chatMessage = JSON.parse(chatMessage.body);
        addChatMessage(chatMessage);
    });
    stompClient.subscribe('/user/queue/game', (gameStatusUpdate) => {
            console.log(gameStatusUpdate);
            updateGameStatus();
        });
    stompClient.subscribe('/user/queue/chat-messages', (chatMessage) => {
                chatMessage = JSON.parse(chatMessage.body);
                addChatMessage(chatMessage);
            });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};
stompClient.activate();
//var messageInput = document.getElementById("chat-message-input");
//
//messageInput.addEventListener("keypress", function(event) {
//  if (event.keyCode === 13) { // Код клавиши Enter
//    onChatMessageClick();
//  }
//});

